package ru.practicum.taskTracker.service;

import ru.practicum.taskTracker.model.Node;
import ru.practicum.taskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    //    private final List<Task> history = new ArrayList<>();
//    private static final int MAX_HISTORY_SIZE = 10;
    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> historyMap = new HashMap<>();

    public void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.task = null;
    }

    public void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node node = head;

        while (node != null) {
            history.add(node.task);
            node = node.next;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            Node node = historyMap.get(task.getId());

            linkLast(task);
            removeNode(node);
        } else {
            linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (!historyMap.containsKey(id)) {
            System.out.println("No such ID in history");
            return;
        }

        Node node = historyMap.remove(id);
        removeNode(node);
    }
}
