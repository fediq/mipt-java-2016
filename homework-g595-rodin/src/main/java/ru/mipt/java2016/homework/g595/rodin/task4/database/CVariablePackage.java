package ru.mipt.java2016.homework.g595.rodin.task4.database;

/**
 * Created by dmitry on 17.12.16.
 */
public class CVariablePackage {

    private String name;

    private String type;

    private String value;

    public CVariablePackage(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
