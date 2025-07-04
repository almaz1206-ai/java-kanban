import java.util.*;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int idCounter = 1;

    // Генерация нового ID
    private int generateId() {
        return idCounter++;
    }

    // Добавление задачи
    public void addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
    }

    // Возвращает все задачи
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTask = new ArrayList<>();
        for(Task task : tasks.values()) {
            if(!(task instanceof Epic || task instanceof Subtask)) {
                allTask.add(task);
            }
        }
        return allTask;
    }

    // Добаляет эпик
    public void addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epic.setSubtasksList(makeSubtasksListById(epic.getId()));
        epics.put(id, epic);
    }

    // Возвращает все эпики
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for(Epic epic : epics.values()) {
            if(epic != null) {
                allEpics.add(epic);
            }
        }
        return allEpics;
    }

    // Добавляет подзадачи
    public void addSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        int epicId = subtask.getEpicId();
        if(!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }

        subtasks.put(id, subtask);
        Epic epic = epics.get(epicId);
        epic.setSubtasksList(makeSubtasksListById(epicId));
    }

    // Получаем подзадачи эпиков
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        if(!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Эпик с id " + epic.getId() + " не найден.");
        }

        return epic.getSubtasksList();
    }

    // Удаляет все задачи
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаляет все эпики
    public void deleteAllEpics() {
        epics.clear();
    }

    // Удаляет все подзадачи
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer key : epics.keySet()) {
            Epic epic = epics.get(key);
            epic.setStatus(Status.NEW);
            epic.setSubtasksList(null);
            epics.put(key, epic);
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
        epics.remove(id);
    }

    //Удаляет подзадачи по идентификатору
    public void deleteSubtaskById(int id) {
        if(!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Подзадачи с таким идентификатором нет");
        }
        Subtask subtask = subtasks.get(id);
        int epicId = subtask.getEpicId();
        subtasks.remove(id);

        Epic epic = epics.get(epicId);
        epic.setSubtasksList(makeSubtasksListById(epicId));
        epics.put(epicId, epic);
    }

    // Обновление задачи
    public void updateTask(Task updatedTask) {
        Task existing = tasks.get(updatedTask.getId());
        if (existing == null || existing instanceof Epic || existing instanceof Subtask) {
            return;
        }
        existing.setName(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
    }

    // Обновление эпика
    public void updateEpic(Epic updatedEpic) {
        Epic existing = epics.get(updatedEpic.getId());
        if (existing == null) return;
        existing.setName(updatedEpic.getTitle());
        existing.setDescription(updatedEpic.getDescription());
        existing.setStatus(updatedEpic.getStatus());
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask updatedSubtask) {
        Subtask existing = subtasks.get(updatedSubtask.getId());
        if (existing == null) return;

        existing.setName(updatedSubtask.getTitle());
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

    public ArrayList<Subtask> makeSubtasksListById(int id) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Integer key : subtasks.keySet()) {
            Subtask subtask = subtasks.get(key);
            if(subtask.getEpicId() == id) {
                subtasksList.add(subtask);
            }
        }
        return subtasksList;
    }
}