package ru.practicum.taskTracker.service;
import ru.practicum.taskTracker.model.Status;
import ru.practicum.taskTracker.model.Task;
import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Subtask;

import java.util.*;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int idCounter = 1;

    // Генерация нового ID
    private int generateId() {
        return idCounter++;
    }

    // Добавление задачи
    public int addTask(Task task) {
        if(task == null) return -1;
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    // Возвращает все задачи
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Возвращает задачу по Id
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Добаляет эпик
    public int addEpic(Epic epic) {
        if(epic == null) return -1;
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    // Возвращает все эпики
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Возвращает эпик по Id
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Добавляет подзадачи
    public int addSubtask(Subtask subtask) {
        if(subtask == null) return -1;

        int epicId = subtask.getEpicId();
        if(!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }
        int id = generateId();
        subtask.setId(id);

        subtasks.put(id, subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtask(subtask);
        updateEpicStatus(epicId);

        return id;
    }

    // Получаем все подзадачи
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Возвращает подзадачи эпиков
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        if(!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }

        return epics.get(epicId).getSubtasksList();
    }

    // Возвращает подзадачу по Id
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Удаляет все задачи
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаляет все эпики
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Удаляет все подзадачи
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.deleteAllSubtasks();
        }
    }

    // Удаляет задачу по идентификатору
    public void deleteTaskById(int id) {
        if(!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Такой задачи нет");
        }
        tasks.remove(id);
    }

    // Удаляет эпик по идентификатору
    public void deleteEpicById(int id) {
        if(!epics.containsKey(id)) {
            throw new IllegalArgumentException("Эпика с таким идентификатором нет");
        }

        Epic epic = epics.get(id);

        for (Subtask subtask : epic.getSubtasksList()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    //Удаляет подзадачи по идентификатору
    public void deleteSubtaskById(int id) {
        if(!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Подзадачи с таким идентификатором нет");
        }
        Subtask subtask = subtasks.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskById(subtask);
        subtasks.remove(id);
        updateEpicStatus(epicId);
    }

    // Обновление задачи
    public void updateTask(Task updatedTask) {
        if(updatedTask == null || !tasks.containsKey(updatedTask.getId())) {
            return;
        }

        tasks.put(updatedTask.getId(), updatedTask);
    }

    // Обновление эпика
    public void updateEpic(Epic updatedEpic) {
        Epic existing = epics.get(updatedEpic.getId());
        if (existing == null) return;
        existing.setName(updatedEpic.getName());
        existing.setDescription(updatedEpic.getDescription());
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask updatedSubtask) {
        if(updatedSubtask == null || !subtasks.containsKey(updatedSubtask.getId())) {
            return;
        }
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        int epicId = updatedSubtask.getEpicId();
        epics.get(epicId).updateSubtaskById(updatedSubtask);
        updateEpicStatus(epicId);
    }

    // Обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subs = getSubtasksByEpic(epic.getId());
        if (subs.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask sub : subs) {
            if (sub.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (sub.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }
}