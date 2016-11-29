package ru.mipt.java2016.homework.g594.sharuev.task4;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "variable")
public class Variable {

    @DatabaseField(id = true)
    String name;
    @DatabaseField
    double value;

    public Variable() {}
    public Variable(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name + " = "+value;
    }
}
