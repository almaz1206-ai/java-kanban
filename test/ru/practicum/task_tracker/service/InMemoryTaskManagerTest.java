package ru.practicum.task_tracker.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    void shouldGetPrioritizedTasks() {
        Task task3 = new Task("Task 3 - 12:00", "Description");
        task3.setStartTime(LocalDateTime.of(2023, 1, 1, 12, 0));
        task3.setDuration(Duration.ofMinutes(59));

        Task task1 = new Task("Task 1 - 10:00", "Description");
        task1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(59));

        Task task2 = new Task("Task 2 - 11:00", "Description");
        task2.setStartTime(LocalDateTime.of(2023, 1, 1, 11, 0));
        task2.setDuration(Duration.ofMinutes(50));

        taskManager.addTask(task3);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size(), "Должно быть 3 задачи");
        assertEquals("Task 1 - 10:00", prioritizedTasks.get(0).getName(), "Первая задача должна быть самой ранней");
        assertEquals("Task 2 - 11:00", prioritizedTasks.get(1).getName(), "Вторая задача по времени");
        assertEquals("Task 3 - 12:00", prioritizedTasks.get(2).getName(), "Третья задача должна быть самой поздней");

        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), prioritizedTasks.get(0).getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 11, 0), prioritizedTasks.get(1).getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), prioritizedTasks.get(2).getStartTime());
    }
}