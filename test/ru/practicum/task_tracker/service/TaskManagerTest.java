package ru.practicum.task_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Status;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.model.Task;

import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();


    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldCreateTask() {
        Task task = new Task("Test", "Description");
        int taskId = taskManager.addTask(task);

        assertNotNull(taskManager.getTaskById(taskId));
        assertEquals(task, taskManager.getTaskById(taskId));
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Test task", "Desc");
        int taskId = taskManager.addTask(task);

        Task updatedTask = new Task("Updated Title", "Updated Desc");
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);
        Task savedTask = taskManager.getTaskById(taskId);
        assertEquals(updatedTask.getName(), savedTask.getName());
        assertEquals(updatedTask.getDescription(), savedTask.getDescription());
    }

    @Test
    void shouldDeleteTask() {
        Task task1 = new Task("Task 1", "Desc");
        Task task2 = new Task("Task 2", "Desc 2");

        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);

        assertNotNull(taskManager.getTaskById(taskId1), "Задача должна существовать до удаления");
        assertEquals(2, taskManager.getAllTasks().size(), "Должно быть 2 задачи");

        taskManager.deleteTaskById(taskId1);

        assertNull(taskManager.getTaskById(taskId1), "Задача должна быть удалена");
        assertEquals(1, taskManager.getAllTasks().size(), "Должна остаться 1 задача");

        assertThrows(IllegalArgumentException.class, () -> taskManager.deleteTaskById(taskId1),
                "Повторное удаление должно вызывать исключение");

    }

    @Test
    void shouldGetAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        int id1 = taskManager.addTask(task1);
        int id2 = taskManager.addTask(task2);

        List<Task> allTasks = taskManager.getAllTasks();

        assertEquals(2, allTasks.size(), "Должно быть 2 задачи");
        assertTrue(allTasks.stream().anyMatch(t -> t.getId() == id1));
        assertTrue(allTasks.stream().anyMatch(t -> t.getId() == id2));
    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task("Test Task", "Description");
        int taskId = taskManager.addTask(task);

        Task foundTask = taskManager.getTaskById(taskId);

        assertNotNull(foundTask, "Задача должна быть найдена");
        assertEquals(taskId, foundTask.getId());
        assertEquals("Test Task", foundTask.getName());

        assertNull(taskManager.getTaskById(999), "Несуществующая задача должна возвращать null");
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getAllTasks().size(), "Должно быть 2 задачи до удаления");

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    void shouldCreateEpic() {
        Epic epic = new Epic("Test Epic", "Test Description");
        int epicId = taskManager.addEpic(epic);

        assertTrue(epicId > 0, "ID эпика должен быть положительным");
        assertNotNull(taskManager.getEpicById(epicId), "Эпик должен быть сохранен");
        assertEquals(epic.getName(), taskManager.getEpicById(epicId).getName());
        assertEquals(epic.getDescription(), taskManager.getEpicById(epicId).getDescription());
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus(), "Статус нового эпика должен быть NEW");
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic("Original Title", "Original Description");
        int epicId = taskManager.addEpic(epic);

        Epic updatedEpic = new Epic("Updated Title", "Updated Description");
        updatedEpic.setId(epicId);

        taskManager.updateEpic(updatedEpic);

        Epic savedEpic = taskManager.getEpicById(epicId);
        assertEquals("Updated Title", savedEpic.getName());
        assertEquals("Updated Description", savedEpic.getDescription());
    }

    @Test
    void shouldDeleteEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        assertNotNull(taskManager.getEpicById(epicId), "Эпик должен существовать до удаления");

        taskManager.deleteEpicById(epicId);

        assertNull(taskManager.getEpicById(epicId), "Эпик должен быть удален");
        assertThrows(IllegalArgumentException.class, () -> taskManager.deleteEpicById(epicId),
                "Повторное удаление должно вызывать исключение");
    }

    @Test
    void shouldGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");

        int id1 = taskManager.addEpic(epic1);
        int id2 = taskManager.addEpic(epic2);

        List<Epic> allEpics = taskManager.getAllEpics();

        assertEquals(2, allEpics.size(), "Должно быть 2 эпика");
        assertTrue(allEpics.stream().anyMatch(e -> e.getId() == id1));
        assertTrue(allEpics.stream().anyMatch(e -> e.getId() == id2));
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Epic foundEpic = taskManager.getEpicById(epicId);

        assertNotNull(foundEpic, "Эпик должен быть найден");
        assertEquals(epicId, foundEpic.getId());
        assertEquals("Test Epic", foundEpic.getName());

        assertNull(taskManager.getEpicById(999), "Несуществующий эпик должен возвращать null");
    }

    @Test
    void shouldDeleteAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description");
        Epic epic2 = new Epic("Epic 2", "Description");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        assertEquals(2, taskManager.getAllEpics().size(), "Должно быть 2 эпика до удаления");

        taskManager.deleteAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все подзадачи также должны быть удалены");
    }

    @Test
    void shouldCreateSubtask() {
        Epic epic = new Epic("Parent Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        assertTrue(subtaskId > 0, "ID подзадачи должен быть положительным");
        assertNotNull(taskManager.getSubtaskById(subtaskId), "Подзадача должна быть сохранена");
        assertEquals(epicId, taskManager.getSubtaskById(subtaskId).getEpicId(),
                "Подзадача должна ссылаться на правильный эпик");
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = new Epic("Parent Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Original", "Desc", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Updated", "Updated Desc", epicId);
        updatedSubtask.setId(subtaskId);
        updatedSubtask.setStatus(Status.IN_PROGRESS);

        taskManager.updateSubtask(updatedSubtask);

        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertEquals("Updated", savedSubtask.getName());
        assertEquals("Updated Desc", savedSubtask.getDescription());
        assertEquals(Status.IN_PROGRESS, savedSubtask.getStatus());
        assertEquals(epicId, savedSubtask.getEpicId());
    }

    @Test
    void shouldDeleteSubtask() {
        Epic epic = new Epic("Parent Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Desc", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        assertNotNull(taskManager.getSubtaskById(subtaskId), "Подзадача должна существовать до удаления");

        taskManager.deleteSubtaskById(subtaskId);

        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадача должна быть удалена");

        Epic parentEpic = taskManager.getEpicById(epicId);
        assertNotNull(parentEpic);
        assertEquals(Status.NEW, parentEpic.getStatus(), "Эпик без подзадач должен иметь статус NEW");
    }

    @Test
    void shouldGetAllSubtasks() {
        Epic epic = new Epic("Test Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc1", epicId);
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epicId);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(2, taskManager.getAllSubtasks().size(), "Должно быть 2 подзадачи");

        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустой");

        Epic updatedEpic = taskManager.getEpicById(epicId);
        assertTrue(updatedEpic.getSubtasksList().isEmpty(), "Список подзадач эпика должен быть пустой");
        assertEquals(Status.NEW, updatedEpic.getStatus(), "Статус эпика без подзадач должен быть NEW");
    }

    @Test
    void shouldGetSubtaskById() {
        Epic epic = new Epic("Parent Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Desc", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        Subtask foundSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(foundSubtask, "Подзадача должна быть найдена");
        assertEquals(subtaskId, foundSubtask.getId());
        assertEquals("Test Subtask", foundSubtask.getName());
        assertEquals(epicId, foundSubtask.getEpicId());

        assertNull(taskManager.getSubtaskById(999), "Несуществующая подзадача должна возвращать null");
    }

    @Test
    void shouldDeleteAllSubtasks() {
        Epic epic = new Epic("Test Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc1", epicId);
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epicId);
        Subtask subtask3 = new Subtask("Sub3", "Desc3", epicId);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        assertEquals(3, taskManager.getAllSubtasks().size(), "Должно быть 3 подзадачи");
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустой");
        assertTrue(epic.getSubtasksList().isEmpty(), "Список подзадач эпика должен быть пустой");
    }

    @Test
    void shouldGetEpicSubtasks() {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", epicId);
        Subtask subtask2 = new Subtask("Sub2", "Desc", epicId);
        Subtask subtask3 = new Subtask("Sub3", "Desc", epicId);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertNotNull(taskManager.getAllSubtasks(), "Список подзадач не может быть null");
        assertEquals(3, taskManager.getAllSubtasks().size(), "Должно быть 3 подзадачи");
        taskManager.getAllSubtasks().forEach(subtask -> assertEquals(epicId, subtask.getEpicId(), "Подзадача должна принадлежать правильному эпику"));
    }

    @Test
    void shouldCalculateEpicStatus() {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", epicId);
        Subtask subtask2 = new Subtask("Sub2", "Desc", epicId);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicById(epicId).getStatus());
    }


}