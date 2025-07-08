package ru.practicum.taskTracker.Epic;

import ru.practicum.taskTracker.Subtask.Subtask;
import ru.practicum.taskTracker.Task.Task;

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

    public void deleteSubtaskById(int id) {
        int index = 0;
        for(Subtask subtask : subtasksList) {
            if(id == subtask.getId()) {
                index = subtasksList.indexOf(subtask);
            }
        }
        subtasksList.remove(index);
    }

    public void updateSubtaskById(Subtask updatedSubtask) {
        for (Subtask subtask : subtasksList) {
            if(subtask.getId() == updatedSubtask.getId()) {
                subtask.setDescription(updatedSubtask.getDescription());
                subtask.setName(updatedSubtask.getName());
            }
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}