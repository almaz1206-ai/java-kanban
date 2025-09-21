package ru.practicum.task_tracker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.task_tracker.model.Task;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetHistory(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    public void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            List<Task> history = taskManager.getHistory();
            String response = GSON.toJson(history);

            sendText(exchange, response, 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
