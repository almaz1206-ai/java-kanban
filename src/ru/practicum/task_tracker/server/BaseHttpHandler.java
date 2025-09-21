package ru.practicum.task_tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.task_tracker.adapter.DurationAdapter;
import ru.practicum.task_tracker.adapter.LocalDateTimeAdapter;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] resp = response.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        String response = String.format("{\"error\": \"%s\"}", message);
        sendText(exchange, response, 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        String response = "{\"error\": \"Задача пересекается с существующими\"}";
        sendText(exchange, response, 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "{\"error\": \"Внутренняя ошибка сервера\"}";
        sendText(exchange, response, 500);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\"}";
        byte[] resp = response.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(400, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected String readText(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = "{\"error\": \"Метод не поддерживается\"}";
        sendText(exchange, response, 405);
    }
}
