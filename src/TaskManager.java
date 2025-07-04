import java.util.*;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private int idCounter = 1;

    // Генерация нового ID
    private int generateId() {
        return idCounter++;
    }

    // Добавление задачи
    public int addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    // Добавление эпика
    public int addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        tasks.put(id, epic);
        epics.put(id, epic);
        return id;
    }

    // Добавляет подзадачу
    public int addSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);

        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }

        tasks.put(id, subtask);
        subtasks.put(id, subtask);
        epics.get(epicId).addSubtaskId(id);

        updateEpicStatus(epicId);

        return id;
    }

    // Получение задачи по ID
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Удаление задачи по ID
    public void removeTaskById(int id) {
        Task task = tasks.get(id);
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                tasks.remove(subtaskId);
            }
            epics.remove(id);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
            subtasks.remove(id);
        }
        tasks.remove(id);
    }

    // Удалить все задачи
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        idCounter = 1;
    }

    // Получить все задачи
    public Collection<Task> getAllTasks() {
        List<Task> all = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (!(task instanceof Epic || task instanceof Subtask)) {
                all.add(task);
            }
        }
        return all;
    }

    // Получить все эпики
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    // Получить все подзадачи
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    // Получить подзадачи эпика
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> result = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    // Обновление задачи
    public void updateTask(Task updatedTask) {
        Task existing = tasks.get(updatedTask.getId());
        if (existing == null || existing instanceof Epic || existing instanceof Subtask) {
            return;
        }
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
    }

    // Обновление эпика
    public void updateEpic(Epic updatedEpic) {
        Epic existing = epics.get(updatedEpic.getId());
        if (existing == null) return;
        existing.setTitle(updatedEpic.getTitle());
        existing.setDescription(updatedEpic.getDescription());
        existing.setStatus(updatedEpic.getStatus());
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask updatedSubtask) {
        Subtask existing = subtasks.get(updatedSubtask.getId());
        if (existing == null) return;

        existing.setTitle(updatedSubtask.getTitle());
        existing.setDescription(updatedSubtask.getDescription());
        existing.setStatus(updatedSubtask.getStatus());

        updateEpicStatus(existing.getEpicId());
    }

    // Обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subs = getSubtasksByEpic(epic);
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