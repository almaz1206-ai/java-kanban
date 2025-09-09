package ru.practicum.task_tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void recalculateEpicTime() {
        if (subtasksList.isEmpty()) {
            this.startTime = null;
            this.duration = null;
            this.endTime = null;
            return;
        }

        this.startTime = subtasksList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.duration = subtasksList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        this.endTime = subtasksList.stream()
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                ", subtasksList=" + subtasksList +
                '}';
    }
}