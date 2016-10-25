package ru.mipt.java2016.homework.g596.hromov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by igorhromov on 15.10.16.
 */

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private Map<K, V> dataBase;
    private File dataBaseFile;
    private Path dataBasePath;
    private int numberOfChanges;


    private void readDataBaseFromDisk() throws IOException {
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(new FileInputStream(dataBaseFile.toString()));
        } catch (FileNotFoundException e) {
            throw new IOException("File not found");
        }
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            dataBase = (Map<K, V>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Read file error");
        }
    }

    MyKeyValueStorage(String path) {
        numberOfChanges = 0;
        dataBasePath = Paths.get(path);
        dataBase = new HashMap<K, V>();
        if (path.charAt(path.length() - 1) != '/') {
            path = path + '/';
        }
        File dataBaseFile = new File(path + "storage.db");
        if (dataBaseFile.exists()) {
            try {
                readDataBaseFromDisk();
            } catch (IOException e) {
                System.out.print("asd");
            }
        } else {
            try{
                dataBasePath = Files.createFile(dataBaseFile.toPath());
            } catch (IOException e) {
                System.out.print("asd");
            }
        }
    }

    @Override
    public boolean exists(K key) {
        return dataBase.containsKey(key);
    }

    @Override
    public V read(K key) {
        return dataBase.get(key);
    }

    @Override
    public void write(K key, V value) {
        dataBase.put(key, value);
    }

    @Override
    public void delete(K key) {
            dataBase.remove(key);
    }

    @Override
    public Iterator readKeys() throws ConcurrentModificationException {
        return dataBase.entrySet().iterator();
    }

    @Override
    public int size() {
        return dataBase.size();
    }

    @Override
    public void close() throws IOException {
        DataOutputStream outputStream;
        try {
            outputStream = new DataOutputStream(new FileOutputStream(dataBasePath.toString()));
        } catch (FileNotFoundException e) {
            throw new IOException("File not found");
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(dataBase);
        objectOutputStream.close();
        outputStream.close();
    }
}