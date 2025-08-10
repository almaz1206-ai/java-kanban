package ru.practicum.task_tracker.service;

import ru.practicum.task_tracker.model.Node;
import ru.practicum.task_tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> historyMap = new HashMap<>();

    private void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }
        node.setTask(null);
    }

    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        historyMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node node = head;

        while (node != null) {
            history.add(node.getTask());
            node = node.getNext();
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (historyMap.containsKey(task.getId())) {
            removeNode(historyMap.get(task.getId()));
        }

        linkLast(task);

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
