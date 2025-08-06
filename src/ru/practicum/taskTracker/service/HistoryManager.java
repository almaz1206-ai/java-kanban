package ru.practicum.taskTracker.service;

import ru.practicum.taskTracker.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
