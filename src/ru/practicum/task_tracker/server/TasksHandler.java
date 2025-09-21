package ru.practicum.task_tracker.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.task_tracker.exceptions.NotFoundException;
import ru.practicum.task_tracker.exceptions.TimeOverlapException;
import ru.practicum.task_tracker.model.Task;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

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
                    } else if (pathParts.length == 2) {
                        handleDeleteAllTasks(exchange);
                    } else {
                        sendNotFound(exchange, "Запрашиваемый ресурс не существует");
                    }
                    break;
                default:
                    sendMethodNotAllowed(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Неверный формат id задачи");
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    public void handleGetAllTasks(HttpExchange exchange) throws IOException {
        try {
            List<Task> taskList = taskManager.getAllTasks();
            String response = GSON.toJson(taskList);
            sendText(exchange, response, 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }

    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        try {
            Task task = taskManager.getTaskById(id);
            String response = GSON.toJson(task);
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Задача не найдена");
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Task task;

        try {
            task = GSON.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный JSON");
            return;
        }

        try {
            if (task.getId() == 0) {
                int createdTaskId = taskManager.addTask(task);
                Task saved = taskManager.getTaskById(createdTaskId);
                String response = GSON.toJson(saved);
                sendText(exchange, response, 201);
            } else {
                taskManager.updateTask(task);
                Task updated = taskManager.getTaskById(task.getId());
                String response = GSON.toJson(updated);
                sendText(exchange, response, 200);
            }
        } catch (TimeOverlapException e) {
            sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Задача не найдена");
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.deleteTaskById(id);
            sendText(exchange, "{\"message\": \"Задача удалена\"}", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Задача не найдена");
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllTasks();
            sendText(exchange, "{\"message\": \"Все задачи удалены\"}", 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
