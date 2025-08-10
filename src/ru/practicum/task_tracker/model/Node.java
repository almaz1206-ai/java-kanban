package ru.practicum.task_tracker.model;

public class Node {
    private Task task;
    private Node next;
    private Node prev;

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

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
