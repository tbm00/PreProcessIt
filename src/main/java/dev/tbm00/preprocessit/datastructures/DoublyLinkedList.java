package dev.tbm00.preprocessit.datastructures;

import dev.tbm00.preprocessit.StaticUtil;

public class DoublyLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Add a node to the end of the list
    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) { // List is empty
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setBack(tail);
            tail = newNode;
        }
        size++;
    }

    // Add a node to the beginning of the list
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) { // List is empty
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head.setBack(newNode);
            head = newNode;
        }
        size++;
    }

    // Remove a node from the end of the list
    public T removeLast() {
        if (tail == null) {
            StaticUtil.log("List is empty");
            return null;
        }
        T data = tail.getData();
        if (head == tail) { // Only one element
            head = null;
            tail = null;
        } else {
            tail = tail.getBack();
            tail.setNext(null);
        }
        size--;
        return data;
    }

    // Remove a node from the beginning of the list
    public T removeFirst() {
        if (head == null) {
            StaticUtil.log("List is empty");
            return null;
        }
        T data = head.getData();
        if (head == tail) { // Only one element
            head = null;
            tail = null;
        } else {
            head = head.getNext();
            head.setBack(null);
        }
        size--;
        return data;
    }

    // Get the size of the list
    public int size() {
        return size;
    }

    // Check if the list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Display the list from head to tail
    public String getForwards() {
        Node<T> current = head;
        String output = "";
        while (current != null) {
            output = output + (current.getData());
            if (current.getNext() != null) {
                output = output + ",";
            }
            current = current.getNext();
        }
        return output;
    }

    // Display the list from tail to head
    public String getBackwards() {
        Node<T> current = tail;
        String output = "";
        while (current != null) {
            output = output + (current.getData());
            if (current.getBack() != null) {
                output = output + ",";
            }
            current = current.getBack();
        }
        return output;
    }

    // Get the head node of the list.
    public Node<T> getHead() {
        return head;
    }

    // Get the tail node of the list.
    public Node<T> getTail() {
        return tail;
    }
}

