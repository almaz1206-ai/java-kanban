package ru.practicum.task_tracker.service;

import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {


    @Test
    void testSaveThroughPublicMethods() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(
                Managers.getDefaultHistory(), tempFile);

        // Вызываем публичные методы, которые запускают save()
        Task task = new Task("Test Task", "Description");
        int taskId = manager.addTask(task); // Вызывает save() внутри

        Epic epic = new Epic("Test Epic", "Epic Description");
        int epicId = manager.addEpic(epic); // Вызывает save() внутри

        // Проверяем, что файл создан и содержит данные
        assertTrue(tempFile.exists());

        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("Test Task"));
        assertTrue(content.contains("Test Epic"));
    }
}