package ru.practicum.taskTracker.model;

import java.util.*;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasksList;

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