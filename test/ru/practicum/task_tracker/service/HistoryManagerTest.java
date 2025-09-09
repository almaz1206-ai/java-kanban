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
    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();

        // Создаем тестовые задачи
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        task3 = new Task("Task 3", "Description 3");
        task3.setId(3);

        epic = new Epic("Epic", "Epic Description");
        epic.setId(4);

        subtask = new Subtask("Subtask", "Subtask Description", epic.getId());
        subtask.setId(5);
    }

    @Test
    void historyPreserversTaskData() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(task1, history.get(0));
    }

    @Test
    void add() {
        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }

    @Test
    void addSameTaskTwiceShouldMoveItToEnd() {
        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(epic, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void removeShouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(epic, history.get(0));
    }

    @Test
    void removeNonExistentIdShouldNotAffectHistory() {
        historyManager.add(task1);
        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void getHistoryShouldReturnTasksInOrderOfView() {
        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task1, epic, subtask), history);
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть null");
        assertTrue(history.isEmpty(), "История должна быть пустой при отсутствии просмотров");
    }

    @Test
    void shouldMoveDuplicateTaskToEndOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1); // Дублирование - должно переместить в конец

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "Должно быть 3 уникальные задачи");
        assertEquals(task2, history.get(0), "Первая задача должна остаться на месте");
        assertEquals(task3, history.get(1), "Вторая задача должна остаться на месте");
        assertEquals(task1, history.get(2), "Дублированная задача должна переместиться в конец");
    }

    @Test
    void shouldRemoveFromMiddleOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаляем из середины
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "Должно остаться 2 задачи");
        assertEquals(task1, history.get(0), "Первая задача должна остаться на месте");
        assertEquals(task3, history.get(1), "Третья задача должна остаться на месте");
        assertFalse(history.contains(task2), "Вторая задача должна быть удалена");
    }
}