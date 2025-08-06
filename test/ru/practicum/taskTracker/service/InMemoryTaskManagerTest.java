package ru.practicum.taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Subtask;
import ru.practicum.taskTracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task1;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        task1 = new Task("Task 1", "Task 1 desc");
        task1.setId(1);
        epic = new Epic("Epic", "Epic desc");
        epic.setId(1);
        subtask1 = new Subtask("Subtask 1", "Subtask 1 desc", epic.getId());
        subtask2 = new Subtask("Subtask 2", "Subtask 2 desc", epic.getId());
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