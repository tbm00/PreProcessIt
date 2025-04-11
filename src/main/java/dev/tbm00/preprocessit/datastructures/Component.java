package dev.tbm00.preprocessit.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Components hold attributes
 */
public class Component {
    private int id;
    private String name;
    private ArrayList<Attribute> attributes;
    private List<String> attributeOrder;

    public Component(int id, String name, ArrayList<Attribute> attributes, List<String> attributeOrder) {
        this.id = id;
        this.name = name;
        this.attributes = attributes;
        this.attributeOrder = attributeOrder;
        //StaticUtil.log("component: " + id + " " + name + " " + attributes);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<String> getAttributeOrder() {
        return attributeOrder;
    }

    public void setAttributeOrder(List<String> attributeOrder) {
        this.attributeOrder = attributeOrder;
    }
}
