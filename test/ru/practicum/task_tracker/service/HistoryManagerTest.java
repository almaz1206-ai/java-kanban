package ru.practicum.task_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.model.Task;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Task", "Task desc");
        task.setId(1);
        epic = new Epic("Epic", "Epic desc");
        epic.setId(2);
        subtask = new Subtask("Subtask", "Subtask desc", epic.getId());
    }

    @Test
    void historyPreserversTaskData() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(task, history.get(0));
    }

    @Test
    void add() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }

    @Test
    void addSameTaskTwiceShouldMoveItToEnd() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(epic, history.get(0));
        assertEquals(task, history.get(1));
    }

    @Test
    void removeShouldRemoveTaskFromHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(epic, history.get(0));
    }

    @Test
    void removeNonExistentIdShouldNotAffectHistory() {
        historyManager.add(task);
        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void getHistoryShouldReturnTasksInOrderOfView() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task, epic, subtask), history);
    }

}