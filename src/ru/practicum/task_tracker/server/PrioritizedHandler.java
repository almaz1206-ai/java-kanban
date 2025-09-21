package ru.practicum.task_tracker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.task_tracker.model.Task;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = GSON.toJson(prioritizedTasks);
                sendText(exchange, response, 200);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        } finally {
            exchange.close();
        }
    }
}
