package ru.practicum.task_tracker.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void taskWithSameIdNotShouldBeEquals() {
        Task task1 = new Task("Задача", "Описание");
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setId(1);
        assertNotEquals(task1, task2);
        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void taskWithDifferentIdsNotShouldBeEquals() {
        Task task1 = new Task("Задача", "Описание");
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setId(2);

        assertNotEquals(task1, task2);
    }

}