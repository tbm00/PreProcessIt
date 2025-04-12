package dev.tbm00.preprocessit.data;

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

    // Get the size of the list
    public int size() {
        return size;
    }

    // Check if the list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Get the head node of the list.
    public Node<T> getHead() {
        return head;
    }

    // Get the tail node of the list.
    public Node<T> getTail() {
        return tail;
    }

    /**
     * Returns the node that is offset positions ahead of the given node.
     * Returns null if the offset goes beyond the list.
     *
     * @param node the starting node
     * @param offset the number of positions to move forward
     * @return the node at the offset position or null if not available.
     */
    public Node<T> getNext(Node<T> node, int offset) {
        if (node == null || offset < 1) {
            return node;
        }
        Node<T> current = node;
        for (int i = 0; i < offset && current != null; i++) {
            current = current.getNext();
        }
        return current;
    }

    /**
     * Starting from the given node, find and return the first unprocessed node.
     * Assumes that type T is Token.
     *
     * @param start the node to begin from
     * @return the first node whose token is not processed, or null if none is found
     */
    public Node<T> getNextUnprocessed(Node<T> start) {
        Node<T> current = start;
        while (current != null) {
            Token token = (Token) current.getData();
            if (!token.isProcessed()) {
                return current;
            }
            current = current.getNext();
        }
        return null;
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

    /**
     * Inserts a new node with the provided data immediately before the given node.
     * @param node the node before which to insert the new node.
     * @param data the data for the new node.
     * @return the newly inserted node.
     */
    public Node<T> addBefore(Node<T> node, T data) {
        if (node == null) {
            throw new IllegalArgumentException("Reference node cannot be null");
        }
        Node<T> newNode = new Node<>(data);
        newNode.setBack(node.getBack());
        newNode.setNext(node);
        if (node.getBack() != null) {
            node.getBack().setNext(newNode);
        } else {
            head = newNode;
        }
        node.setBack(newNode);
        size++;
        return newNode;
    }

    /**
     * Inserts a new node with the provided data right after the given node.
     * @param node the node after which to insert the new node.
     * @param data the data for the new node.
     * @return the newly inserted node.
     */
    public Node<T> addAfter(Node<T> node, T data) {
        if (node == null) {
            throw new IllegalArgumentException("Reference node cannot be null");
        }
        Node<T> newNode = new Node<>(data);
        newNode.setNext(node.getNext());
        newNode.setBack(node);
        if (node.getNext() != null) {
            node.getNext().setBack(newNode);
        } else {
            tail = newNode;
        }
        node.setNext(newNode);
        size++;
        return newNode;
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
    
    /**
     * Removes a specified node from the list.
     * @param node the node to remove.
     */
    public void remove(Node<T> node) {
        if (node == null) {
            return;
        }
        if (node == head && node == tail) { // Only one element in the list.
            head = null;
            tail = null;
        } else if (node == head) { // Removing the head.
            head = head.getNext();
            head.setBack(null);
        } else if (node == tail) { // Removing the tail.
            tail = tail.getBack();
            tail.setNext(null);
        } else { // Removing from the middle.
            Node<T> previous = node.getBack();
            Node<T> next = node.getNext();
            previous.setNext(next);
            next.setBack(previous);
        }
        size--;
    }
    
    // Returns the list from head to tail
    public String getForwards() {
        Node<T> current = head;
        StringBuilder output = new StringBuilder();
        while (current != null) {
            output.append(current.getData());
            if (current.getNext() != null) {
                output.append(",");
            }
            current = current.getNext();
        }
        return output.toString();
    }

    // Returns the list from tail to head
    public String getBackwards() {
        Node<T> current = tail;
        StringBuilder output = new StringBuilder();
        while (current != null) {
            output.append(current.getData());
            if (current.getBack() != null) {
                output.append(",");
            }
            current = current.getBack();
        }
        return output.toString();
    }

    /**
     * Merges the token in the given node with the next node's token.
     * Assumes that the generic type T is of type Token.
     * After merging, the next node is removed from the list.
     * @param node the node whose value will be merged with the next node.
     */
    public void mergeWithNext(Node<T> node) {
        if (node != null && node.getNext() != null) {
            Node<T> nextNode = node.getNext();
            // Merge token value (current token appends the next token's value)
            ((Token)node.getData()).mergeWith((Token)nextNode.getData());
            // Remove the next node from the linked list.
            remove(nextNode);
        }
    }
    
    /**
     * Merges the token in the given node with the previous node's token.
     * Assumes that the generic type T is of type Token.
     * After merging, the current node is removed from the list.
     * @param node the node whose value will be merged with the previous node.
     */
    public void mergeWithPrevious(Node<T> node) {
        if (node != null && node.getBack() != null) {
            Node<T> previousNode = node.getBack();
            // Merge token value (previous token appends the current token's value)
            ((Token)previousNode.getData()).mergeWith((Token)node.getData());
            // Remove the current node from the linked list.
            remove(node);
        }
    }
}
