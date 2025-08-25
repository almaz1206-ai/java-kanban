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
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File testFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        testFile = File.createTempFile("test_tasks", ".csv");
        manager = new FileBackedTaskManager(Managers.getDefaultHistory(), testFile);
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    private FileBackedTaskManager loadManager() {
        return FileBackedTaskManager.loadFromFile(testFile);
    }

    @Test
    void testSaveThroughPublicMethods() throws IOException {
        testFile = File.createTempFile("test", ".csv");
        testFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(
                Managers.getDefaultHistory(), testFile);

        Task task = new Task("Test Task", "Description");
        int taskId = manager.addTask(task);

        Epic epic = new Epic("Test Epic", "Epic Description");
        int epicId = manager.addEpic(epic);

        assertTrue(testFile.exists());

        String content = Files.readString(testFile.toPath());
        assertTrue(content.contains("Test Task"));
        assertTrue(content.contains("Test Epic"));
    }

    @Test
    void testFileContentStructure() throws IOException {
        testFile = File.createTempFile("structure", "csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(
                Managers.getDefaultHistory(), testFile);

        Task task = new Task("Test Task", "Test Description");
        manager.addTask(task);

        String content = Files.readString(testFile.toPath());

        assertTrue(content.startsWith("id,type,name,status,description,epic"));
        assertTrue(content.contains("TASK"));
        assertTrue(content.contains("Test Task"));
        assertTrue(content.contains("NEW"));
        assertTrue(content.contains("Test Description"));
    }

    @Test
    void testSaveAndLoadTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        int id1 = manager.addTask(task1);
        int id2 = manager.addTask(task2);

        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        FileBackedTaskManager loadedManager = loadManager();

        assertEquals(2, loadedManager.getAllTasks().size());

        Task loadedTask1 = loadedManager.getTaskById(id1);
        assertNotNull(loadedTask1);
        assertEquals("Task 1", loadedTask1.getName());
        assertEquals("Description 1", loadedTask1.getDescription());
        assertEquals(Status.IN_PROGRESS, loadedTask1.getStatus());

        Task loadedTask2 = loadedManager.getTaskById(id2);
        assertNotNull(loadedTask2);
        assertEquals("Task 2", loadedTask2.getName());
        assertEquals(Status.NEW, loadedTask2.getStatus());
    }

    @Test
    void testSubtaskEpicLinksRestored() {
        Epic epic1 = new Epic("Epic 1", "Desc 1");
        Epic epic2 = new Epic("Epic 2", "Desc 2");
        int epic1Id = manager.addEpic(epic1);
        int epic2Id = manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Sub 1", "Desc", epic1Id);
        Subtask subtask2 = new Subtask("Sub 2", "Desc", epic2Id);
        int subtask1Id = manager.addSubtask(subtask1);
        int subtask2Id = manager.addSubtask(subtask2);

        FileBackedTaskManager loadedManager = loadManager();

        Subtask loadedSubtask1 = loadedManager.getSubtaskById(subtask1Id);
        Subtask loadedSubtask2 = loadedManager.getSubtaskById(subtask2Id);

        assertEquals(epic1Id, loadedSubtask1.getEpicId());
        assertEquals(epic2Id, loadedSubtask2.getEpicId());

        Epic loadedEpic1 = loadedManager.getEpicById(epic1Id);
        Epic loadedEpic2 = loadedManager.getEpicById(epic2Id);

        assertEquals(1, loadedEpic1.getSubtasksList().size());
        assertEquals(1, loadedEpic2.getSubtasksList().size());
        assertEquals(subtask1Id, loadedEpic1.getSubtasksList().get(0).getId());
        assertEquals(subtask2Id, loadedEpic2.getSubtasksList().get(0).getId());
    }

    @Test
    void testAllOperations() {
        Task task = new Task("Task", "Desc");
        Epic epic = new Epic("Epic", "Desc");
        int taskId = manager.addTask(task);
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId);
        int subtaskId = manager.addSubtask(subtask);

        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        manager.getTaskById(taskId);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);

        FileBackedTaskManager loadedManager = loadManager();

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        Task loadedTask = loadedManager.getTaskById(taskId);
        assertEquals(Status.IN_PROGRESS, loadedTask.getStatus());

        Subtask loadedSubtask = loadedManager.getSubtaskById(subtaskId);
        assertEquals(Status.DONE, loadedSubtask.getStatus());
        assertEquals(epicId, loadedSubtask.getEpicId());

        Epic loadedEpic = loadedManager.getEpicById(epicId);
        assertEquals(Status.DONE, loadedEpic.getStatus());
        assertEquals(1, loadedEpic.getSubtasksList().size());
    }
}