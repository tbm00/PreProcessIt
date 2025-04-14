package dev.tbm00.preprocessit.model.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Components hold attributes
 */
public class Component {
    private int id;
    private String name;
    private ArrayList<Attribute> attributes;
    private List<String> attributeOutputOrder;

    public Component(int id, String name, ArrayList<Attribute> attributes, List<String> attributeOutputOrder) {
        this.id = id;
        this.name = name;
        this.attributes = attributes;
        this.attributeOutputOrder = attributeOutputOrder;
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
        return attributeOutputOrder;
    }

    public void setAttributeOrder(List<String> attributeOutputOrder) {
        this.attributeOutputOrder = attributeOutputOrder;
    }
}
