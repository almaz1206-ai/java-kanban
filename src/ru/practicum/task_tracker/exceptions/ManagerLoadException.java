package ru.practicum.task_tracker.exceptions;

import java.io.IOException;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(String message, IOException cause) {
        super(message);
    }

    public ManagerLoadException(String message) {
        super(message);
    }
}
