package ru.mipt.java2016.homework.g594.stepanov.task2;


import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.HashMap;

public class SerializationFactory {
    SerializationFactory(String keyType, String valueType, String path) {
        if (keyType.equals("Integer") && valueType.equals("Double")) {
            try {
                serializator = new IntegerDoubleSerializator(path);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            cashedValues = new HashMap<Integer, Double>();
        }
        if (keyType.equals("String") && valueType.equals("String")) {
            try {
                serializator = new StringStringSerializator(path);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            cashedValues = new HashMap<String, String>();
        }
        if (keyType.equals("StudentKey") && valueType.equals("Student")) {
            try {
                serializator = new StudentSerializator(path);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            cashedValues = new HashMap<StudentKey, Student>();
        }
    }

    private ObjectSerializator serializator;
    private HashMap cashedValues;

    public ObjectSerializator getSerializator() {
        return serializator;
    }

    public HashMap getValues() {
        return cashedValues;
    }
}
