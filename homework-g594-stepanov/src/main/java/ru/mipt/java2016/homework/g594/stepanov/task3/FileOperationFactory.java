package ru.mipt.java2016.homework.g594.stepanov.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.HashMap;

public class FileOperationFactory {

    private FileOperation fileOperation;
    private HashMap map;

    public FileOperationFactory(String fileName, String keyType, String valueType) {
        if (keyType.equals("Integer") && valueType.equals("Double")) {
            fileOperation = new IntegerDoubleFileOperation(fileName);
            map = new HashMap<Integer, Long>();
        }
        if (keyType.equals("String") && valueType.equals("String")) {
            fileOperation = new StringStringFileOperation(fileName);
            map = new HashMap<String, Long>();
        }
        if (keyType.equals("StudentKey") && valueType.equals("Student")) {
            fileOperation = new StudentFileOperation(fileName);
            map = new HashMap<StudentKey, Long>();
        }
    }

    public FileOperation getFileOperation() {
        return fileOperation;
    }

    public HashMap getMap() {
        return map;
    }

}
