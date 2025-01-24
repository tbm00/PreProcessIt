package dev.tbm00.preprocessit.datastructures;

import java.util.ArrayList;

/**
 * Component for commands
 */
public class Component {
    
    private String name;
    private ArrayList<String> attributes;
    private DoublyLinkedList<String> commands;

    public Component() {
        this.name = "";
        this.attributes = new ArrayList<>();
        this.commands = new DoublyLinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    public DoublyLinkedList<String> getCommands() {
        return commands;
    }

    public void setCommands(DoublyLinkedList<String> commands) {
        this.commands = commands;
    }
}
