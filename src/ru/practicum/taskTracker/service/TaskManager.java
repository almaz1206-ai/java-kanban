package ru.practicum.taskTracker.service;

import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Subtask;
import ru.practicum.taskTracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    // Добавление задачи
    int addTask(Task task);

    // Возвращает все задачи
    ArrayList<Task> getAllTasks();

    // Возвращает задачу по Id
    Task getTaskById(int id);

    // Добаляет эпик
    int addEpic(Epic epic);

    // Возвращает все эпики
    ArrayList<Epic> getAllEpics();

    // Возвращает эпик по Id
    Epic getEpicById(int id);

    // Добавляет подзадачи
    int addSubtask(Subtask subtask);

    // Получаем все подзадачи
    ArrayList<Subtask> getAllSubtasks();

    // Возвращает подзадачи эпиков
    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    // Возвращает подзадачу по Id
    Subtask getSubtaskById(int id);

    // Удаляет все задачи
    void deleteAllTasks();

    // Удаляет все эпики
    void deleteAllEpics();

    // Удаляет все подзадачи
    void deleteAllSubtasks();

    // Удаляет задачу по идентификатору
    void deleteTaskById(int id);

    // Удаляет эпик по идентификатору
    void deleteEpicById(int id);

    //Удаляет подзадачи по идентификатору
    void deleteSubtaskById(int id);

    // Обновление задачи
    void updateTask(Task updatedTask);

    // Обновление эпика
    void updateEpic(Epic updatedEpic);

    // Обновление подзадачи
    void updateSubtask(Subtask updatedSubtask);

    // Возвращает массив с иторией
    List<Task> getHistory();
}
