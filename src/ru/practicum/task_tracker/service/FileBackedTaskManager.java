package ru.practicum.task_tracker.service;

import ru.practicum.task_tracker.exceptions.ManagerLoadException;
import ru.practicum.task_tracker.exceptions.ManagerSaveException;
import ru.practicum.task_tracker.model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    static final String HEADER = "id,type,name,status,description,epicId \n";

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    // Сохраняет состояние менеджера задач в файл
    private void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.write(HEADER);
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Task epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Task subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл");
        }
    }

    private String toString(Task task) {
        String[] fields = {
                String.valueOf(task.getId()),
                String.valueOf(task.getType()),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : ""
        };
        return String.join(",", fields);
    }

    // Восстановление менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        fileManager.load();
        return fileManager;
    }

    // Загружает состояние менеджера задач из файла
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {

                Task task = fromString(line);

                if (task != null) {
                    restoreTask(task);
                }
            }

        } catch (FileNotFoundException e) {
            throw new ManagerLoadException("Ошибка! Файл не найден!", e);
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка чтения из файла, возможно файл поврежден", e);
        }
    }

    private void restoreTask(Task task) {
        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            subtasks.put(task.getId(), (Subtask) task);
            // Восстанавливаем связь с эпиком
            Epic epic = epics.get(((Subtask) task).getEpicId());
            if (epic != null) {
                epic.addSubtask((Subtask) task);
            }
        } else {
            tasks.put(task.getId(), task);
        }

        // Обновляем счетчик ID
        if (task.getId() >= idCounter) {
            idCounter = task.getId() + 1;
        }
    }

    // Восстановление задачи из строки
    private Task fromString(String val) {
        String[] fields = val.split(",", -1);
        if (fields.length < 5) return null;

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                if (fields.length < 6 || fields[5].isEmpty()) return null;
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new ManagerSaveException("Неизвестный тип объекта" + type);
        }
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}
