package ru.practicum.task_tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.service.InMemoryTaskManager;
import ru.practicum.task_tracker.service.Managers;
import ru.practicum.task_tracker.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        epic = new Epic("Test Epic", "Test Description");
        int epicId = taskManager.addEpic(epic);
        epic = taskManager.getEpicById(epicId);
    }

    @Test
    void epicHasNewStatusWhenSubtaskListIsEmpty() {
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicsWithSameIdNotShouldBeEquals() {
        Epic epic1 = new Epic("Epic 1", "Epic 1 desc");
        epic1.setId(100);
        epic1.setStatus(Status.IN_PROGRESS);

        Epic epic2 = new Epic("Epic 2", "Epic 2 desc");
        epic2.setId(100);
        epic2.setStatus(Status.DONE);

        assertEquals(epic2, epic1);
        assertEquals(epic2.hashCode(), epic1.hashCode());
    }

    @Test
    void epicCannotAddItselfAsSubtask() {
        epic.setId(1);

        Subtask subtask = new Subtask("Подзадача", "Эпик добавляется как подзадача", epic.getId());
        subtask.setId(100);

        epic.addSubtask(subtask);

        assertEquals(1, epic.getSubtasksList().size());
        assertTrue(epic.getSubtasksList().contains(subtask));

        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    void shouldReturnNewWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epic.getId());

        // Статус NEW устанавливается по умолчанию
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus(),
                "Статус должен быть NEW когда все подзадачи NEW");
    }

    @Test
    void shouldReturnDoneWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epic.getId());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus(),
                "Статус должен быть DONE когда все подзадачи DONE");
    }

    @Test
    void shouldReturnInProgressWhenMixedNewAndDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epic.getId());

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.NEW);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус должен быть IN_PROGRESS при смеси NEW и DONE");
    }

    @Test
    void shouldReturnInProgressWhenOneNewAndOneDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус должен быть IN_PROGRESS при одной NEW и одной DONE");
    }

    @Test
    void shouldReturnNewWhenNoSubtasks() {
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus(),
                "Статус должен быть NEW при отсутствии подзадач");
    }

    @Test
    void shouldUpdateStatusWhenSubtaskAdded() {
        Epic initialEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, initialEpic.getStatus());

        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.addSubtask(subtask);

        Epic epicAfterAdd = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, epicAfterAdd.getStatus(),
                "Статус эпика должен обновиться при добавлении подзадачи");
    }
}