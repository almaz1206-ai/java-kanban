package ru.practicum.taskTracker.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ru.practicum.taskTracker.model.Task;

class TaskTest {

    @Test
    void taskWithSameIdShouldBeEquals() {
        Task task1 = new Task("Задача", "Описание");
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setId(1);
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void taskWithDifferentIdsShouldNotBeEquals() {
        Task task1 = new Task("Задача", "Описание");
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setId(2);

        assertNotEquals(task1, task2);
    }

}