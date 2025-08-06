package ru.practicum.taskTracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void subtasksWithSameIdNotShouldBeEqual() {
        Subtask subtask1 = new Subtask("Sub 1", "Sub 1 desc", 1);
        subtask1.setId(100);
        subtask1.setStatus(Status.IN_PROGRESS);

        Subtask subtask2 = new Subtask("Sub 2", "Sub 2 desc", 1);
        subtask2.setId(100);
        subtask2.setStatus(Status.DONE);

        assertNotEquals(subtask1, subtask2);
        assertNotEquals(subtask1.hashCode(), subtask2.hashCode());
    }

    @Test
    void subtasksWithSameIdButDifferentFieldNotShouldEquals() {
        Subtask subtask1 = new Subtask("Sub 1", "Sub 1 desc", 1);
        subtask1.setId(100);

        Subtask subtask2 = new Subtask("Sub 2", "Sub 2 desc", 1);
        subtask2.setId(101);

        assertNotEquals(subtask1, subtask2);
    }

}