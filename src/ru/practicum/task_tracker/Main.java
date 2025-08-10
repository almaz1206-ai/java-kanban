package ru.practicum.task_tracker;

import ru.practicum.task_tracker.model.Epic;
import ru.practicum.task_tracker.model.Subtask;
import ru.practicum.task_tracker.model.Task;
import ru.practicum.task_tracker.service.Managers;
import ru.practicum.task_tracker.service.TaskManager;

import java.util.List;

public class Main {

    public static void main(String[] args) {

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
        Subtask sub3 = new Subtask("Поклеить обои", "Гостиная", epic1.getId());

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


        // 4. Имитируем просмотр в разном порядке
        System.out.println("1. Запрашиваем задачи и эпики в разном порядке...\n");

        manager.getTaskById(task1.getId());
        printHistory(manager);

        manager.getEpicById(epic1.getId());
        printHistory(manager);

        manager.getSubtaskById(sub2.getId());
        printHistory(manager);

        manager.getTaskById(task2.getId());
        printHistory(manager);

        manager.getEpicById(epic2.getId());
        printHistory(manager);

        // Повторный запрос — проверим, что нет дубликатов
        System.out.println("2. Повторно запрашиваем задачу и эпик...\n");

        manager.getTaskById(task1.getId());
        printHistory(manager);

        manager.getEpicById(epic1.getId());
        printHistory(manager);

        // 5. Удаляем обычную задачу
        System.out.println("3. Удаляем задачу: " + task1 + "\n");
        manager.deleteTaskById(task1.getId());
        printHistory(manager);

        System.out.println("4. Удаляем подзадачу: " + sub2 + "\n");
        manager.deleteSubtaskById(sub2.getId());
        printHistory(manager);

        // 6. Удаляем эпик с подзадачами
        System.out.println("5. Удаляем эпик с подзадачами: " + epic1 + "\n");
        manager.deleteEpicById(epic1.getId());
        printHistory(manager);
    }

    // Вспомогательный метод для вывода истории
    private static void printHistory(TaskManager manager) {
        System.out.println("Текущая история просмотров:");
        List<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            System.out.println("   История пуста");
        } else {
            for (Task t : history) {
                System.out.println("   - " + t);
            }
        }
        System.out.println(); // пустая строка для читаемости
    }
}
