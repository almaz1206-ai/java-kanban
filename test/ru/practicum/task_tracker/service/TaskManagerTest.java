package ru.practicum.task_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Status;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.model.Task;

import java.util.List;

class TaskManagerTest {

    TaskManager manager;
    TaskManager taskManager;
    Task task1;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskManager = Managers.getDefault();
        task1 = new Task("Task 1", "Task 1 desc");
        task1.setId(1);
        epic = new Epic("Epic", "Epic desc");
        epic.setId(1);
        subtask1 = new Subtask("Subtask 1", "Subtask 1 desc", epic.getId());
        subtask2 = new Subtask("Subtask 2", "Subtask 2 desc", epic.getId());
    }

    @Test
    void taskWithGeneratedIdAndTaskWithSetIdShouldNotConflict() {
        Task task1 = new Task("Task 1", "Task 1 desc");
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Task 2 desc");
        task2.setId(1);
        manager.addTask(task2);

        assertNotEquals(task2, task1);
    }

    @Test
    void taskManagerCanAddAndGetEpics() {
        Epic epic = new Epic("Epic", "Epic desc");
        manager.addEpic(epic);

        Epic found = manager.getEpicById(epic.getId());

        assertNotNull(found);
        assertEquals(epic, found);
    }

    @Test
    void taskManagerCanAddAndGetSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        manager.addSubtask(subtask);

        Subtask found = manager.getSubtaskById(subtask.getId());

        assertNotNull(found);
        assertEquals(subtask, found);
    }

    @Test
    void taskShouldNotChangeAfterAddingToManager() {
        Task task = new Task("Task", "Task desc");
        task.setStatus(Status.IN_PROGRESS);

        manager.addTask(task);

        Task found = manager.getTaskById(task.getId());

        assertEquals(task.getId(), found.getId());
        assertEquals(task.getName(), found.getName());
        assertEquals(task.getDescription(), found.getDescription());
        assertEquals(task.getStatus(), found.getStatus());
    }

    @Test
    void getTaskByIdShouldAddToHistory() {
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void getEpicByIdShouldAddToHistory() {
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(epic, history.get(0));
    }

    @Test
    void getSubtaskByIdShouldAddToHistory() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(subtask1, history.get(0));
    }

    @Test
    void deleteEpicShouldRemoveAllSubtasksAndFromHistory() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        int epicId = epic.getId();
        int sub1Id = subtask1.getId();

        taskManager.deleteEpicById(epicId);

        assertNull(taskManager.getEpicById(epicId));
        assertNull(taskManager.getSubtaskById(sub1Id));

        List<Task> history = taskManager.getHistory();
        assertFalse(history.stream().anyMatch(t -> t.getId() == epicId));
        assertFalse(history.stream().anyMatch(t -> t.getId() == sub1Id));
    }
}