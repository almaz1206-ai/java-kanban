package ru.practicum.taskTracker.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Subtask;
import ru.practicum.taskTracker.model.Task;
import ru.practicum.taskTracker.service.HistoryManager;
import ru.practicum.taskTracker.service.InMemoryHistoryManager;
import ru.practicum.taskTracker.service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void historyPreserversTaskData() {
        Task task = new Task("Task", "Desc");
        task.setId(1);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(task, history.get(0));
    }

    @Test
    void add() {
        Task task = new Task("Task", "Task desc");
        Epic epic = new Epic("Epic", "Epic desc");
        Subtask subtask = new Subtask("Subtask", "Subtask desc", epic.getId());

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }


}