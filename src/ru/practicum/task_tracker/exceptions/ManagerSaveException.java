package ru.practicum.task_tracker.exceptions;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message, IOException cause) {
        super(message);
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}
