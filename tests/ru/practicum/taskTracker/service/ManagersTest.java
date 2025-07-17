package ru.practicum.taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Status;
import ru.practicum.taskTracker.model.Subtask;


class ManagersTest {
    TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void managersShouldReturnInitializedInstance() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager);
        assertNotNull(historyManager);
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
}