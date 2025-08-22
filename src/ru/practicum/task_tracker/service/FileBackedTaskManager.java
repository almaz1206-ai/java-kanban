package ru.practicum.task_tracker.service;

import ru.practicum.task_tracker.exceptions.ManagerSaveException;
import ru.practicum.task_tracker.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    // Сохраняет состояние менеджера задач в файл
    private void save() {
        try (PrintWriter record = new PrintWriter(new FileWriter(file))) {
            record.write("id,type,name,status,description,epicId \n");
            for (Task task : getAllTasks()) {
                record.write(toString(task) + "\n");
            }

            for (Task subtask : getAllSubtasks()) {
                record.write(toString(subtask) + "\n");
            }

            for (Task epic : getAllEpics()) {
                record.write(toString(epic) + "\n");
            }

            record.write("\n");
            List<Task> history = getHistory();
            if (!history.isEmpty()) {
                record.write(historyToString(historyManager));
            }


        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл");
        }
    }

    private String toString(Task task) {
        String[] fields = {
                String.valueOf(task.getId()),
                String.valueOf(getType(task)),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : ""
        };
        return String.join(",", fields);
    }

    // Определяет тип задачи
    private TaskType getType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else {
            return TaskType.TASK;
        }
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
            // Читаем историю
            String historyLine = reader.readLine();
            if (historyLine != null && !historyLine.isEmpty()) {
                restoreHistory(historyLine);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Ошибка! Файл не найден!", e);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка чтения из файла, возможно файл поврежден", e);
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

    private void restoreHistory(String historyLine) {
        List<Integer> historyIds = historyFromString(historyLine);
        for (int id : historyIds) {
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            } else if (subtasks.containsKey(id)) {
                historyManager.add(subtasks.get(id));
            }
        }
    }

    public static List<Integer> historyFromString(String val) {
        String[] split = val.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.valueOf(split[i]));
        }
        return list;
    }

    // Преобразует историю просмотра задач в строку для сохранения в файл
    public static String historyToString(HistoryManager historyManager) {
        List<Task> list = historyManager.getHistory();
        String record = "";
        int counter = 0;
        for (Task task : list) {
            if (counter < list.size()) {
                record = record + task.getId() + ",";
            } else {
                record = record + task.getId();
            }
            counter++;
        }
        return record;
    }

    // Восстановление задачи из строки
    private Task fromString(String val) {
        String[] fields = val.split(",");
        if (fields.length < 5) return null;

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setStatus(status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setStatus(status);
                epic.setId(id);
                return epic;
            case SUBTASK:
                if (fields.length < 6) return null;
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setStatus(status);
                subtask.setId(id);
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
