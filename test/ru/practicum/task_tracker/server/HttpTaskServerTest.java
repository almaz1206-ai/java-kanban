package ru.practicum.task_tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.adapter.DurationAdapter;
import ru.practicum.task_tracker.adapter.LocalDateTimeAdapter;
import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Status;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.model.Task;
import ru.practicum.task_tracker.service.InMemoryTaskManager;
import ru.practicum.task_tracker.service.Managers;
import ru.practicum.task_tracker.service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static final String BASE_URL = "http://localhost:8080";
    private final TaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private HttpTaskServer server;
    private HttpClient client;

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        server = new HttpTaskServer(manager);
        server.start();

        Thread.sleep(100);

        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Add", "Описание задачи",
                Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1));

        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Задача должна создаваться с кодом 201");

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals("Test Add", tasks.get(0).getName());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("For Get", "Получить по ID",
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));
        int id = manager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task retrieved = gson.fromJson(response.body(), Task.class);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals("For Get", retrieved.getName());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("To Update", "Будет обновлена",
                Duration.ofMinutes(10), LocalDateTime.now().plusHours(3));
        int id = manager.addTask(task);

        task.setName("Обновлённое имя");
        task.setStatus(Status.DONE);
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task updated = manager.getTaskById(id);
        assertEquals("Обновлённое имя", updated.getName());
        assertEquals(Status.DONE, updated.getStatus());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("To Delete", "",
                Duration.ofMinutes(5), LocalDateTime.now().plusHours(4));
        int id = manager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getTaskById(id));
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 1", "", Duration.ofMinutes(10), LocalDateTime.now()));
        manager.addTask(new Task("Task 2", "", Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(20)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Новый Эпик", "Описание эпика");

        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Эпик должен создаваться с кодом 201");

        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Новый Эпик", epics.get(0).getName());
        assertEquals(Status.NEW, epics.get(0).getStatus());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Для получения", "Получить по ID");
        int id = manager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic retrieved = gson.fromJson(response.body(), Epic.class);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals("Для получения", retrieved.getName());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Для удаления", "");
        int id = manager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getEpicById(id));
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // Сначала создаём эпик
        Epic epic = new Epic("Эпик для подзадачи", "");
        int epicId = manager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask("Подзадача","Описание", epicId);

        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Подзадача", subtasks.get(0).getName());
        assertEquals(epicId, subtasks.get(0).getEpicId());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Тест эпик", "");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Сабтаск", "", epicId);
        int id = manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask retrieved = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals("Сабтаск", retrieved.getName());
    }

    @Test
    public void testGetSubtasksByEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик с подзадачами", "");
        int epicId = manager.addEpic(epic);

        Subtask s1 = new Subtask("S1", "", epicId);
        Subtask s2 = new Subtask("S2", "", epicId);

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + epicId + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String body = response.body();
        Type listType = new TypeToken<List<Subtask>>(){}.getType();
        List<Subtask> subtasks = gson.fromJson(body, listType);
        assertEquals(2, subtasks.size());
        assertEquals("S1", subtasks.get(0).getName());
        assertEquals("S2", subtasks.get(1).getName());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Для удаления", "", epicId);
        int id = manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getSubtaskById(id));

        // Убедимся, что эпик остался
        assertNotNull(manager.getEpicById(epicId));
    }
}