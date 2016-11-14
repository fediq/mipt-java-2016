package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyKeyValueStorage<Key, Value> implements KeyValueStorage<Key, Value> {

    private final HashMap<Key, Value> actualCopy = new HashMap<>();
    private String keyType;
    private String valueType;
    private MySerialization keySerializator;
    private MySerialization valueSerializator;
    private String pathDirectory;
    private String fullPath;
    private boolean isOpen;

    public MyKeyValueStorage(String path, String keyT, String valueT) throws Exception {
        keyType = keyT;
        valueType = valueT;
        pathDirectory = path;
        File file = new File(pathDirectory);
        if (!file.isDirectory()) {
            throw new RuntimeException("invalid directory path");
        }
        if (!file.exists()) {
            throw new RuntimeException("such directory does not exist");
        }

        fullPath = pathDirectory + File.separator + "MyStorage";
        isOpen = true;

        keySerializator = Serializations.takeSerializer(keyType);
        valueSerializator = Serializations.takeSerializer(valueType);

        File file2 = new File(fullPath);
        if (!file2.exists()) {
            file2.createNewFile();
        } else {
            FileInputStream in = new FileInputStream(fullPath);
            DataInputStream fileIn = new DataInputStream(in);
            int num = fileIn.readInt(); // read number of pairs(key/value) in storage
            String fileKeyType = fileIn.readUTF(); // read the type of keys
            String fileValueType = fileIn.readUTF(); // read the type of values
            if (!fileKeyType.equals(keyType) || !fileValueType.equals(valueType)) {
                throw new RuntimeException("This file contains other types of objects");
            }
            Key key;
            Value value;
            for (int i = 0; i < num; ++i) { // read all pairs from file
                key = (Key) keySerializator.readFromFile(fileIn);
                value = (Value) valueSerializator.readFromFile(fileIn);
                actualCopy.put(key, value); // add pair to hashMap
            }
        }

    }

    public MyKeyValueStorage(String path, MySerialization externalKeySerializator,
                             MySerialization externalValueSerializator) throws Exception {
        keyType = "StudentKey";
        valueType = "Student";
        pathDirectory = path;
        File file = new File(pathDirectory);
        if (!file.isDirectory()) {
            throw new RuntimeException("invalid directory path");
        }
        if (!file.exists()) {
            throw new RuntimeException("such directory does not exist");
        }

        fullPath = pathDirectory + File.separator + "MyStorage";
        isOpen = true;

        keySerializator = externalKeySerializator;
        valueSerializator = externalValueSerializator;

        File file2 = new File(fullPath);
        if (!file2.exists()) {
            file2.createNewFile();
        } else {
            FileInputStream in = new FileInputStream(fullPath);
            DataInputStream fileIn = new DataInputStream(in);
            int num = fileIn.readInt(); // read number of pairs(key/value) in storage
            String fileKeyType = fileIn.readUTF(); // read the type of keys
            String fileValueType = fileIn.readUTF(); // read the type of values
            if (!fileKeyType.equals(keyType) || !fileValueType.equals(valueType)) {
                throw new RuntimeException("This file contains other types of objects");
            }
            Key key;
            Value value;
            for (int i = 0; i < num; ++i) { // read all pairs from file
                key = (Key) keySerializator.readFromFile(fileIn);
                value = (Value) valueSerializator.readFromFile(fileIn);
                actualCopy.put(key, value); // add pair to hashMap
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
        return actualCopy.keySet().iterator(); // return iterator to the set of pairs
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
        outFile.writeInt(actualCopy.size()); // write number of pairs(key/value) in storage
        outFile.writeUTF(keyType); // write the type of keys
        outFile.writeUTF(valueType); // write the type of values
        for (Map.Entry<Key, Value> i : actualCopy.entrySet()) { // write all pairs to file
            keySerializator.writeToFile(i.getKey(), outFile);
            valueSerializator.writeToFile(i.getValue(), outFile);
        }
        isOpen = false;
        outFile.close();
        out.close();
    }
}
