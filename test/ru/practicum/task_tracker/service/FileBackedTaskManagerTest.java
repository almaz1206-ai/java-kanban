package ru.practicum.task_tracker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Status;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            testFile = File.createTempFile("test", "csv");
            return new FileBackedTaskManager(Managers.getDefaultHistory(), testFile);
        } catch (IOException e) {
            throw new RuntimeException("", e);
        }
    }

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @AfterEach
    void tearDown() {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    @Override
    void shouldCreateTask() {
        Task task = new Task("Test Task", "Description");
        int taskId = taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Task loadedTask = loadedManager.getTaskById(taskId);

        assertNotNull(loadedTask);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(taskId, loadedTask.getId());
    }

    @Test
    @Override
    void shouldUpdateTask() {
        Task task = new Task("Original", "Description");
        int taskId = taskManager.addTask(task);

        Task updatedTask = new Task("Updated", "New Description");
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Task loadedTask = loadedManager.getTaskById(taskId);

        assertEquals("Updated", loadedTask.getName());
        assertEquals("New Description", loadedTask.getDescription());
    }

    @Test
    @Override
    void shouldDeleteTask() {
        Task task = new Task("To Delete", "Description");
        int taskId = taskManager.addTask(task);

        assertNotNull(taskManager.getTaskById(taskId));
        taskManager.deleteTaskById(taskId);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertNull(loadedManager.getTaskById(taskId));
    }

    @Test
    @Override
    void shouldGetAllTasks() {
        Task task1 = new Task("Task 1", "Desc");
        Task task2 = new Task("Task 2", "Desc");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        List<Task> loadedTasks = loadedManager.getAllTasks();

        assertEquals(2, loadedTasks.size());
        assertTrue(loadedTasks.stream().anyMatch(t -> t.getName().equals("Task 1")));
        assertTrue(loadedTasks.stream().anyMatch(t -> t.getName().equals("Task 2")));
    }

    @Test
    @Override
    void shouldGetTaskById() {
        Task task = new Task("Test Task", "Description");
        int taskId = taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Task loadedTask = loadedManager.getTaskById(taskId);

        assertNotNull(loadedTask);
        assertEquals(taskId, loadedTask.getId());
        assertEquals("Test Task", loadedTask.getName());
    }

    @Test
    @Override
    void shouldDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Desc");
        Task task2 = new Task("Task 2", "Desc");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteAllTasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loadedManager.getAllTasks().isEmpty());
    }

    @Test
    @Override
    void shouldCreateEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Epic loadedEpic = loadedManager.getEpicById(epicId);

        assertNotNull(loadedEpic);
        assertEquals(epic.getName(), loadedEpic.getName());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
    }

    @Test
    @Override
    void shouldUpdateEpic() {
        Epic epic = new Epic("Original", "Desc");
        int epicId = taskManager.addEpic(epic);

        Epic updatedEpic = new Epic("Updated", "New Desc");
        updatedEpic.setId(epicId);
        taskManager.updateEpic(updatedEpic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Epic loadedEpic = loadedManager.getEpicById(epicId);

        assertEquals("Updated", loadedEpic.getName());
        assertEquals("New Desc", loadedEpic.getDescription());
    }

    @Test
    @Override
    void shouldDeleteEpic() {
        Epic epic = new Epic("To Delete", "Desc");
        int epicId = taskManager.addEpic(epic);

        taskManager.deleteEpicById(epicId);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertNull(loadedManager.getEpicById(epicId));
    }

    @Test
    @Override
    void shouldGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Desc");
        Epic epic2 = new Epic("Epic 2", "Desc");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        List<Epic> loadedEpics = loadedManager.getAllEpics();

        assertEquals(2, loadedEpics.size());
    }

    @Test
    @Override
    void shouldGetEpicById() {
        Epic epic = new Epic("Test Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Epic loadedEpic = loadedManager.getEpicById(epicId);

        assertNotNull(loadedEpic);
        assertEquals(epicId, loadedEpic.getId());
    }

    @Test
    @Override
    void shouldDeleteAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Desc");
        Epic epic2 = new Epic("Epic 2", "Desc");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.deleteAllEpics();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    @Override
    void shouldCreateSubtask() {
        Epic epic = new Epic("Parent Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Subtask loadedSubtask = loadedManager.getSubtaskById(subtaskId);

        assertNotNull(loadedSubtask);
        assertEquals("Test Subtask", loadedSubtask.getName());
        assertEquals(epicId, loadedSubtask.getEpicId());

        Epic loadedEpic = loadedManager.getEpicById(epicId);
        assertTrue(loadedEpic.getSubtasksList().stream()
                .anyMatch(sub -> sub.getId() == subtaskId));
    }

    @Test
    @Override
    void shouldUpdateSubtask() {
        Epic epic = new Epic("Parent", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Original", "Desc", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Updated", "New Desc", epicId);
        updatedSubtask.setId(subtaskId);
        updatedSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(updatedSubtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Subtask loadedSubtask = loadedManager.getSubtaskById(subtaskId);

        assertEquals("Updated", loadedSubtask.getName());
        assertEquals("New Desc", loadedSubtask.getDescription());
        assertEquals(Status.IN_PROGRESS, loadedSubtask.getStatus());
    }

    @Test
    @Override
    void shouldDeleteSubtask() {
        Epic epic = new Epic("Parent", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("To Delete", "Desc", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        taskManager.deleteSubtaskById(subtaskId);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertNull(loadedManager.getSubtaskById(subtaskId));

        Epic loadedEpic = loadedManager.getEpicById(epicId);
        assertTrue(loadedEpic.getSubtasksList().stream()
                .noneMatch(sub -> sub.getId() == subtaskId));
    }

    @Test
    @Override
    void shouldGetAllSubtasks() {
        Epic epic = new Epic("Parent", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub 1", "Desc", epicId);
        Subtask subtask2 = new Subtask("Sub 2", "Desc", epicId);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        List<Subtask> loadedSubtasks = loadedManager.getAllSubtasks();

        assertEquals(2, loadedSubtasks.size());
    }

    @Test
    @Override
    void shouldGetSubtaskById() {
        Epic epic = new Epic("Parent", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Desc", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Subtask loadedSubtask = loadedManager.getSubtaskById(subtaskId);

        assertNotNull(loadedSubtask);
        assertEquals(subtaskId, loadedSubtask.getId());
    }

    @Test
    @Override
    void shouldDeleteAllSubtasks() {
        Epic epic = new Epic("Parent", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub 1", "Desc", epicId);
        Subtask subtask2 = new Subtask("Sub 2", "Desc", epicId);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.deleteAllSubtasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loadedManager.getAllSubtasks().isEmpty());

        Epic loadedEpic = loadedManager.getEpicById(epicId);
        assertTrue(loadedEpic.getSubtasksList().isEmpty());
    }

    @Test
    @Override
    void shouldGetEpicSubtasks() {
        Epic epic = new Epic("Parent", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub 1", "Desc", epicId);
        Subtask subtask2 = new Subtask("Sub 2", "Desc", epicId);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        List<Subtask> loadedSubtasks = loadedManager.getAllSubtasks();

        assertEquals(2, loadedSubtasks.size());
        for (Subtask subtask : loadedSubtasks) {
            assertEquals(epicId, subtask.getEpicId());
        }
    }

    @Test
    @Override
    void shouldCalculateEpicStatus() {
        Epic epic = new Epic("Test Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub 1", "Desc", epicId);
        Subtask subtask2 = new Subtask("Sub 2", "Desc", epicId);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Epic loadedEpic = loadedManager.getEpicById(epicId);

        assertEquals(Status.DONE, loadedEpic.getStatus());
    }
}