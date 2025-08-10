package ru.practicum.task_tracker.service;

import ru.practicum.task_tracker.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
