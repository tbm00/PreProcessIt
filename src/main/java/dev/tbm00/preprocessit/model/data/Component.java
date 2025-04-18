package dev.tbm00.preprocessit.model.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Components hold attributes & input line rules
 */
public class Component {
    private int id;
    private String name;
    private ArrayList<Attribute> attributes;
    private List<String> attributeOutputOrder;
    private String attributeOutputDelimiter;
    private LineRule inputLineRule;
    private LineRule outputLineRule;
    private boolean appendLeftovers;

    public Component(int id, String name, ArrayList<Attribute> attributes, List<String> attributeOutputOrder, String attributeOutputDelimiter, LineRule inputLineRule, LineRule outputLineRule, boolean appendLeftovers) {
        this.id = id;
        this.name = name;
        this.attributes = attributes;
        this.attributeOutputOrder = attributeOutputOrder;
        this.attributeOutputDelimiter = attributeOutputDelimiter;
        this.inputLineRule = inputLineRule;
        this.outputLineRule = outputLineRule;
        this.appendLeftovers = appendLeftovers;
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

    public String getAttributeDelimiter() {
        return attributeOutputDelimiter;
    }

    public void setAttributeDelimiter(String attributeOutputDelimiter) {
        this.attributeOutputDelimiter = attributeOutputDelimiter;
    }

    public LineRule getInputLineRule() {
        return inputLineRule;
    }

    public void setInputLineRule(LineRule inputLineRule) {
        this.inputLineRule = inputLineRule;
    }

    public LineRule getOutputLineRule() {
        return outputLineRule;
    }

    public void setOututLineRule(LineRule outputLineRule) {
        this.outputLineRule = outputLineRule;
    }

    public boolean getAppendLeftovers() {
        return appendLeftovers;
    }

    public void setAppendLeftovers(boolean appendLeftovers) {
        this.appendLeftovers = appendLeftovers;
    }
}
