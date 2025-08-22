package ru.practicum.task_tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicHasNewStatusWhenSubtaskListIsEmpty() {
        Epic epic = new Epic("Эпик 1", "Описание");
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