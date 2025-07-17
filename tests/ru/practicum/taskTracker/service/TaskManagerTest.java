package ru.practicum.taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Status;
import ru.practicum.taskTracker.model.Subtask;
import ru.practicum.taskTracker.model.Task;


class TaskManagerTest {

    TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
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
}