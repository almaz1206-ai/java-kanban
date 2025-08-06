package ru.practicum.taskTracker.model;

public class Node {
    public Task task;
    public Node next;
    public Node prev;

    public Node(Node prev, Task task, Node next) {
        this.next = next;
        this.prev = prev;
        this.task = task;
    }


    @Override
    public String toString() {
        return "Node{" +
                "task=" + task +
                '}';
    }
}
