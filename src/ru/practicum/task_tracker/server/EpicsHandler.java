package ru.practicum.task_tracker.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.task_tracker.exceptions.NotFoundException;
import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (method.equals("GET") && pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                int epicId = Integer.parseInt(pathParts[2]);
                handleGetEpicSubtasks(exchange, epicId);
                return;
            }

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetAllEpics(exchange);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleGetEpicById(exchange, id);
                    } else {
                        sendNotFound(exchange, "Запрашиваемый ресурс не существует");
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateEpic(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleDeleteEpicById(exchange, id);
                    } else if (pathParts.length == 2) {
                        handleDeleteAllEpics(exchange);
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

    private void handleGetEpicSubtasks(HttpExchange exchange, int id) throws IOException {
        try {
            List<Subtask> subtaskList = taskManager.getSubtasksByEpic(id);
            String response = GSON.toJson(subtaskList);
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Эпик не найден");
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        try {
            List<Epic> epicList = taskManager.getAllEpics();
            String response = GSON.toJson(epicList);
            sendText(exchange, response, 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        try {
            Epic epic = taskManager.getEpicById(id);
            String response = GSON.toJson(epic);
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Эпик не найден");
        }
    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Epic epic;

        try {
            epic = GSON.fromJson(body, Epic.class);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный JSON");
            return;
        }

        try {
            if (epic.getId() == 0) {
                int createdEpicId = taskManager.addEpic(epic);
                Epic saved = taskManager.getEpicById(createdEpicId);
                String response = GSON.toJson(saved);
                sendText(exchange, response, 201);
            } else {
                taskManager.updateEpic(epic);
                Epic updated = taskManager.getEpicById(epic.getId());
                String response = GSON.toJson(updated);
                sendText(exchange, response, 200);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Эпик не найден");
        } catch (Exception e) {
            sendInternalError(exchange);
        }

    }

    private void handleDeleteEpicById(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.deleteEpicById(id);
            sendText(exchange, "{\"message\": \"Эпик удален\"}", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Эпик не найден");
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllEpics();
            sendText(exchange, "{\"message\": \"Все эпики удалены\"}", 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
