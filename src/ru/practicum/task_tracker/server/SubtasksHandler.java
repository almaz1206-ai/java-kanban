package ru.practicum.task_tracker.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.task_tracker.exceptions.NotFoundException;
import ru.practicum.task_tracker.exceptions.TimeOverlapException;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

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
                    } else {
                        sendNotFound(exchange, "Подзадачи с данным идентификатором не существует");
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Неверный формат id");
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

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtaskList = taskManager.getAllSubtasks();
        String response = gson.toJson(subtaskList);
        sendText(exchange, response, 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = taskManager.getSubtaskById(id);
        String response = gson.toJson(subtask);
        sendText(exchange, response, 200);
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getId() == 0) {
            int createdSubtaskId = taskManager.addSubtask(subtask);
            Subtask saved = taskManager.getSubtaskById(createdSubtaskId);
            String response = gson.toJson(saved);
            sendText(exchange, response, 201);
        } else {
            taskManager.updateSubtask(subtask);
            Subtask updated = taskManager.getSubtaskById(subtask.getId());
            String response = gson.toJson(updated);
            sendText(exchange, response, 200);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, int id) throws IOException {
        taskManager.deleteSubtaskById(id);
        sendText(exchange, "{\"message\": \"Подзадача удалена\"}", 200);
    }
}
