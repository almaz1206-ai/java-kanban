package ru.practicum.task_tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasksList;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void addSubtask(Subtask subtask) {
        subtasksList.add(subtask);
    }

    public void deleteAllSubtasks() {
        subtasksList.clear();
    }

    public void deleteSubtaskById(Subtask deletedSubtask) {
        int index = 0;
        for (Subtask subtask : subtasksList) {
            if (deletedSubtask.getId() == subtask.getId()) {
                index = subtasksList.indexOf(subtask);
            }
        }
        subtasksList.remove(index);
    }

    public void updateSubtaskById(Subtask updatedSubtask) {
        int index = 0;
        for (Subtask subtask : subtasksList) {
            if (updatedSubtask.getId() == subtask.getId()) {
                index = subtasksList.indexOf(subtask);
            }
        }
        subtasksList.remove(index);
        subtasksList.add(updatedSubtask);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public Duration getDuration() {
        return subtasksList.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public LocalDateTime getStartTime() {
        return subtasksList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void recalculateEpicTime(List<Subtask> subtasks) {
        List<Subtask> epicSubtasks = subtasks.stream()
                .filter(subtask -> {
                    System.out.println("сабтаски" + subtask);
                    return subtasksList.contains(subtask);
                })
                .collect(Collectors.toList());
        if (epicSubtasks.isEmpty()) {
            this.endTime = null;
            return;
        }

        this.endTime = epicSubtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasksList=" + subtasksList +
                '}';
    }
}