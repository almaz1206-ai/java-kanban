package ru.practicum.task_tracker.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.task_tracker.exceptions.NotFoundException;
import ru.practicum.task_tracker.exceptions.TimeOverlapException;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
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
                            handleGetAllSubtasks(exchange);
                        } else if (pathParts.length == 3) {
                            int id = Integer.parseInt(pathParts[2]);
                            handleGetSubtaskById(exchange, id);
                        } else {
                            sendNotFound(exchange, "Запращиваемый ресурс не существует");
                        }
                        break;
                    case "POST":
                        handleCreateOrUpdateSubtask(exchange);
                        break;
                    case "DELETE":
                        if (pathParts.length == 3) {
                            int id = Integer.parseInt(pathParts[2]);
                            handleDeleteSubtask(exchange, id);
                        } else if (pathParts.length == 2) {
                            handleDeleteAllSubtasks(exchange);
                        } else {
                            sendNotFound(exchange, "Запрашиваемый ресурс не существует");
                        }
                        break;
                    default:
                        exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            List<Subtask> subtaskList = taskManager.getAllSubtasks();
            String response = GSON.toJson(subtaskList);
            sendText(exchange, response, 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        try {
            Subtask subtask = taskManager.getSubtaskById(id);
            String response = GSON.toJson(subtask);
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Подзадача не найдена");
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Subtask subtask;

        try {
            subtask = GSON.fromJson(body, Subtask.class);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный JSON");
            return;
        }

        try {
            if (subtask.getId() == 0) {
                int createdSubtaskId = taskManager.addSubtask(subtask);
                Subtask saved = taskManager.getSubtaskById(createdSubtaskId);
                String response = GSON.toJson(saved);
                sendText(exchange, response, 201);
            } else {
                taskManager.updateSubtask(subtask);
                Subtask updated = taskManager.getSubtaskById(subtask.getId());
                String response = GSON.toJson(updated);
                sendText(exchange, response, 200);
            }
        } catch (TimeOverlapException e) {
            sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Подзадача не найдена");
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.deleteSubtaskById(id);
            sendText(exchange, "{\"message\": \"Подзадача удалена\"}", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Подзадача не найдена");
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllSubtasks();
            sendText(exchange, "{\"message\": \"Все подзадачи удалены\"}", 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
