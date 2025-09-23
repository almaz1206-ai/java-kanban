package ru.practicum.task_tracker.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.task_tracker.exceptions.NotFoundException;
import ru.practicum.task_tracker.exceptions.TimeOverlapException;
import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

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
                    handleCreateEpic(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleDeleteEpicById(exchange, id);
                    } else {
                        sendNotFound(exchange, "Эпика с данным идентификатором не существует");
                    }
                    break;
                default:
                    sendMethodNotAllowed(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Неверный формат id задачи");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный JSON");
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

    private void handleGetEpicSubtasks(HttpExchange exchange, int id) throws IOException {
        List<Subtask> subtaskList = taskManager.getSubtasksByEpic(id);
        String response = gson.toJson(subtaskList);
        sendText(exchange, response, 200);
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epicList = taskManager.getAllEpics();
        String response = gson.toJson(epicList);
        sendText(exchange, response, 200);
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        String response = gson.toJson(epic);
        sendText(exchange, response, 200);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Epic epic = gson.fromJson(body, Epic.class);

        int createdEpicId = taskManager.addEpic(epic);
        Epic saved = taskManager.getEpicById(createdEpicId);
        String response = gson.toJson(saved);
        sendText(exchange, response, 201);
    }

    private void handleDeleteEpicById(HttpExchange exchange, int id) throws IOException {
        taskManager.deleteEpicById(id);
        sendText(exchange, "{\"message\": \"Эпик удален\"}", 200);
    }
}
