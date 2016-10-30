package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by semien on 30.10.16.
 */
public class MyKeyValueStorage<Key, Value> implements KeyValueStorage<Key, Value> {

    private final HashMap<Key, Value> actualCopy = new HashMap<>();
    private String keyType;
    private String valueType;
    private MySerialization keySerializator;
    private MySerialization valueSerializator;
    private String pathDirectory;
    private String fullPath;
    boolean isOpen = false;

    MyKeyValueStorage(String path, String keyT, String valueT) throws IOException {
        keyType = keyT;
        valueType = valueT;
        pathDirectory = path;
        File file = new File(pathDirectory);
        if (!file.isDirectory()) throw new RuntimeException("invalid directory path");
        if (!file.exists()) throw new RuntimeException("such directory does not exist");

        fullPath = pathDirectory + File.separator +"MyStorage";
        isOpen = true;

        switch (keyType) {
            case "Integer":
                keySerializator = MySerialization.MyIntegerSerialization;
                break;
            case "Double":
                keySerializator = MySerialization.MyDoubleSerialization;
                break;
            case "String":
                keySerializator = MySerialization.MyStringSerialization;
                break;
            case "Student":
                keySerializator = MySerialization.MyStudentSerialization;
                break;
            case "StudentKey":
                keySerializator = MySerialization.MyStudentKeySerialization;
                break;
        }
        switch (valueType) {
            case "Integer":
                valueSerializator = MySerialization.MyIntegerSerialization;
                break;
            case "Double":
                valueSerializator = MySerialization.MyDoubleSerialization;
                break;
            case "String":
                valueSerializator = MySerialization.MyStringSerialization;
                break;
            case "Student":
                valueSerializator = MySerialization.MyStudentSerialization;
                break;
            case "StudentKey":
                valueSerializator = MySerialization.MyStudentKeySerialization;
                break;
        }

        File file2 = new File(fullPath);
        if (!file2.exists()) {
            file2.createNewFile();
        } else {
            FileInputStream in = new FileInputStream(fullPath);
            DataInputStream fileIn = new DataInputStream(in);
            int num = (int) MySerialization.MyIntegerSerialization.readFromFile(fileIn);
            String FileKeyType = (String) MySerialization.MyStringSerialization.readFromFile(fileIn);
            String FileValueType = (String) MySerialization.MyStringSerialization.readFromFile(fileIn);
            /*if (FileKeyType != keyType || FileValueType != valueType) {
                throw new RuntimeException("This file contains other types of objects");
            }*/
            Key key;
            Value value;
            for (int i = 0; i < num; ++i) {
                key = (Key) keySerializator.readFromFile(fileIn);
                value = (Value) valueSerializator.readFromFile(fileIn);
                actualCopy.put(key, value);
            }
        }

    }

    @Override
    public Value read(Key key) {
        if (!isOpen) {
            throw new RuntimeException("Storage has been already closed");
        }
        return actualCopy.get(key);
    }

    @Override
    public boolean exists(Key key) {
        if (!isOpen) {
            throw new RuntimeException("Storage has been already closed");
        }
        return actualCopy.containsKey(key);
    }

    @Override
    public void write(Key key, Value value) {
        if (!isOpen) {
            throw new RuntimeException("Storage has been already closed");
        }
        actualCopy.put(key, value);
    }

    @Override
    public void delete(Key key) {
        if (!isOpen) {
            throw new RuntimeException("Storage has been already closed");
        }
        actualCopy.remove(key);
    }

    @Override
    public Iterator<Key> readKeys() {
        if (!isOpen) {
            throw new RuntimeException("Storage has been already closed");
        }
        return actualCopy.keySet().iterator();
    }

    @Override
    public int size() {
        return actualCopy.size();
    }

    @Override
    public void close() throws IOException {
        if (!isOpen) {
            throw new RuntimeException("Storage has been already closed");
        }
        FileOutputStream out = new FileOutputStream(fullPath);
        DataOutputStream outFile = new DataOutputStream(out);
        MySerialization.MyIntegerSerialization.writeToFile(actualCopy.size(), outFile);
        MySerialization.MyStringSerialization.writeToFile(keyType, outFile);
        MySerialization.MyStringSerialization.writeToFile(valueType, outFile);
        for (Map.Entry<Key, Value> i : actualCopy.entrySet()) {
            keySerializator.writeToFile(i.getKey(), outFile);
            valueSerializator.writeToFile(i.getValue(), outFile);
        }
        isOpen = false;
        outFile.close();
        out.close();
    }
}
