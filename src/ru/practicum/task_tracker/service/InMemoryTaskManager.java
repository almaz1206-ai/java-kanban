package ru.practicum.task_tracker.service;

import ru.practicum.task_tracker.exceptions.ManagerValidationException;
import ru.practicum.task_tracker.model.Status;
import ru.practicum.task_tracker.model.Task;
import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Subtask;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId)
    );

    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    protected int idCounter = 1;

    // Генерация нового ID
    protected int generateId() {
        return idCounter++;
    }

    protected void addToPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected void removeFromPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    // Добавление задачи
    @Override
    public int addTask(Task task) {
        if (task == null) return -1;

        if (!isValidTimeSlot(task)) {
            throw new ManagerValidationException("Задача пересекается по времени с существующей");
        }
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        addToPrioritized(task);
        return id;
    }

    // Возвращает все задачи
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Возвращает задачу по Id
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    // Добаляет эпик
    @Override
    public int addEpic(Epic epic) {
        if (epic == null) return -1;
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    // Возвращает все эпики
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Возвращает эпик по Id
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    // Добавляет подзадачи
    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask == null) return -1;

        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }

        if (!isValidTimeSlot(subtask)) {
            throw new ManagerValidationException("Подзадача пересекается по времени с существующей");
        }

        int id = generateId();
        subtask.setId(id);

        subtasks.put(id, subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtask(subtask);
        epic.recalculateEpicTime();
        updateEpicStatus(epicId);
        addToPrioritized(subtask);

        return id;
    }

    // Получаем все подзадачи
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Возвращает подзадачи эпиков
    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }

        return epics.get(epicId).getSubtasksList();
    }

    // Возвращает подзадачу по Id
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    // Удаляет все задачи
    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            if (task != null) {
                removeFromPrioritized(task);
                historyManager.remove(task.getId());
            }
        });
        tasks.clear();
    }

    // Удаляет все эпики
    @Override
    public void deleteAllEpics() {
        subtasks.values().forEach(subtask -> {
            if (subtask != null) {
                removeFromPrioritized(subtask);
                historyManager.remove(subtask.getId());
            }
        });

        epics.keySet().forEach(historyManager::remove);

        epics.clear();
        subtasks.clear();
    }

    // Удаляет все подзадачи
    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtask.getId());
        }

        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.deleteAllSubtasks();
            epic.recalculateEpicTime();
        }
    }

    // Удаляет задачу по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Такой задачи нет");
        }
        removeFromPrioritized(tasks.get(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    // Удаляет эпик по идентификатору
    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Эпика с таким идентификатором нет");
        }

        Epic epic = epics.get(id);

        for (Subtask subtask : epic.getSubtasksList()) {
            removeFromPrioritized(subtask);
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    //Удаляет подзадачи по идентификатору
    @Override
    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Подзадачи с таким идентификатором нет");
        }
        Subtask subtask = subtasks.get(id);
        removeFromPrioritized(subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskById(subtask);
        historyManager.remove(id);
        subtasks.remove(id);
        updateEpicStatus(epicId);
        epic.recalculateEpicTime();
    }

    // Обновление задачи
    @Override
    public void updateTask(Task updatedTask) {
        if (updatedTask == null || !tasks.containsKey(updatedTask.getId())) {
            return;
        }
        Task existingTask = tasks.get(updatedTask.getId());

        if (!isValidTimeSlot(updatedTask)) {
            throw new ManagerValidationException("Обновленная задача пересекается по времени");
        }
        removeFromPrioritized(existingTask);

        tasks.put(updatedTask.getId(), updatedTask);
        addToPrioritized(updatedTask);
    }

    // Обновление эпика
    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic existing = epics.get(updatedEpic.getId());
        if (existing == null) return;
        existing.setName(updatedEpic.getName());
        existing.setDescription(updatedEpic.getDescription());
    }

    // Обновление подзадачи
    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask == null || !subtasks.containsKey(updatedSubtask.getId())) {
            return;
        }
        Subtask existingSubtask = subtasks.get(updatedSubtask.getId());

        if (!isValidTimeSlot(updatedSubtask)) {
            throw new ManagerValidationException("Обновленная подзадача пересекается с существующей");
        }

        if (!epics.containsKey(updatedSubtask.getEpicId())) {
            throw new ManagerValidationException("Эпик с id = " + updatedSubtask.getEpicId() + " не существует");
        }
        removeFromPrioritized(existingSubtask);

        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        addToPrioritized(updatedSubtask);

        int epicId = updatedSubtask.getEpicId();
        epics.get(epicId).updateSubtaskById(updatedSubtask);
        updateEpicStatus(epicId);
        epics.get(epicId).recalculateEpicTime();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

        boolean allNew = true;
        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask sub : subs) {
            if (sub.getStatus() != Status.NEW) {
                allNew = false;
            }

            if (sub.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (sub.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private boolean isValidTimeSlot(Task newTask) {
        if (newTask.getStartTime() == null) {
            return true;
        }

        if (prioritizedTasks.isEmpty()) {
            return true;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null)
                .noneMatch(task -> isTimeOverlapping(newTask, task));

    }

    private boolean isTimeOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
}