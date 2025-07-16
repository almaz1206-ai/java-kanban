package ru.practicum.taskTracker.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Status;
import ru.practicum.taskTracker.model.Subtask;
import ru.practicum.taskTracker.service.Managers;
import ru.practicum.taskTracker.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager manager = Managers.getDefault();

    @Test
    void epicHasNewStatusWhenSubtaskListIsEmpty() {
        Epic epic = new Epic("Эпик 1", "Описание");
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicHasDoneStatusWhenAllSubTasksAreDone() {
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void epicsWithSameIdShouldBeEquals() {
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
        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(1);

        Subtask subtask = new Subtask("Подзадача", "Эпик добавляется как подзадача", epic.getId());
        subtask.setId(100);

        epic.addSubtask(subtask);

        assertEquals(1, epic.getSubtasksList().size());
        assertTrue(epic.getSubtasksList().contains(subtask));

        assertEquals(epic.getId(), subtask.getEpicId());
    }
}