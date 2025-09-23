package ru.practicum.task_tracker.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.task_tracker.exceptions.NotFoundException;
import ru.practicum.task_tracker.exceptions.TimeOverlapException;
import ru.practicum.task_tracker.model.Task;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetAllTasks(exchange);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleGetTaskById(exchange, id);
                    } else {
                        sendNotFound(exchange, "Запрашиваемый ресурс не существует");
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateTask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleDeleteTask(exchange, id);
                    } else {
                        sendNotFound(exchange, "Задачи с данным идентификатором не существует");
                    }
                    break;
                default:
                    sendMethodNotAllowed(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Неверный формат id задачи");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный формат JSON");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeOverlapException e) {
            sendHasOverlaps(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        } finally {
            exchange.close();
        }
    }

    public void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> taskList = taskManager.getAllTasks();
        String response = gson.toJson(taskList);
        sendText(exchange, response, 200);
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        String response = gson.toJson(task);
        sendText(exchange, response, 200);
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            int createdTaskId = taskManager.addTask(task);
            Task saved = taskManager.getTaskById(createdTaskId);
            String response = gson.toJson(saved);
            sendText(exchange, response, 201);
        } else {
            taskManager.updateTask(task);
            Task updated = taskManager.getTaskById(task.getId());
            String response = gson.toJson(updated);
            sendText(exchange, response, 200);
        }
    }

    private void handleDeleteTask(HttpExchange exchange, int id) throws IOException {
        taskManager.deleteTaskById(id);
        sendText(exchange, "{\"message\": \"Задача удалена\"}", 200);
    }
}
