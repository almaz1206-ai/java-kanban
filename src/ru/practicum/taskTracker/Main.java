package ru.practicum.taskTracker;

import ru.practicum.taskTracker.model.Epic;
import ru.practicum.taskTracker.model.Status;
import ru.practicum.taskTracker.model.Subtask;
import ru.practicum.taskTracker.model.Task;
import ru.practicum.taskTracker.service.InMemoryTaskManager;
import ru.practicum.taskTracker.service.Managers;
import ru.practicum.taskTracker.service.TaskManager;

public class Main {

    public static void main(String[] args) {

//        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();

        System.out.println("=== ТЕСТИРУЕМ РАБОТУ С ЗАДАЧАМИ ===");

        // 1. Добавляем обычные задачи
        Task task1 = new Task("Почистить зубы", "Утром после пробуждения");
        Task task2 = new Task("Пробежка", "30 минут бега");
        manager.addTask(task1);
        manager.addTask(task2);

        System.out.println("\nВсе обычные задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        // 2. Добавляем эпики
        Epic epic1 = new Epic("Подготовка к экзамену", "Неделя интенсивной учёбы");
        Epic epic2 = new Epic("Ремонт квартиры", "Капитальный ремонт");
        manager.addEpic(epic1);
        manager.addEpic(epic2);



        // 3. Добавляем подзадачи к эпику
        Subtask sub1 = new Subtask("Выучить тему 1", "Основы Java", epic1.getId());
        Subtask sub2 = new Subtask("Выучить тему 2", "OOP", epic1.getId());
        Subtask sub3 = new Subtask("Поклеить обои", "Гостиная", epic2.getId());

        manager.addSubtask(sub1);
        manager.addSubtask(sub2);
        manager.addSubtask(sub3);

        System.out.println("\nВсе эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask sub : manager.getAllSubtasks()) {
            System.out.println(sub);
        }

        System.out.println("\nПодзадачи эпика '" + epic1.getName() + "':");
        for (Subtask sub : manager.getSubtasksByEpic(epic1.getId())) {
            System.out.println("   " + sub);
        }

        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic1.getId());
        manager.getTaskById(task1.getId());
        manager.getSubtaskById(sub1.getId());
        manager.getSubtaskById(sub1.getId());
        manager.getSubtaskById(sub2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task2.getId());

        System.out.println("\nИстория просмотров:");
        for (Task t : manager.getHistory()) {
            System.out.println(t);
        }

        System.out.println("\nСтатус эпика '" + epic1.getName() + "': " + epic1.getStatus());

        // 4. Обновляем статус подзадачи
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        System.out.println("\nСтатус эпика '" + epic1.getName() + "' после обновления подзадачи: " + epic1.getStatus());

        System.out.println("\nВсе эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        // 5. Обновляем обычную задачу
        task1.setDescription("Обновлённое описание");
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        System.out.println("\nОбновлённая задача:");
        System.out.println(manager.getTaskById(task1.getId()));

        // 6. Удаляем подзадачу
        manager.deleteSubtaskById(sub3.getId());

        System.out.println("\nВсе подзадачи после удаления одной:");
        for (Subtask sub : manager.getAllSubtasks()) {
            System.out.println(sub);
        }

        // 7. Удаляем все задачи
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();

        System.out.println("\nВсе данные после очистки:");
        System.out.println("Задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());
    }
}
