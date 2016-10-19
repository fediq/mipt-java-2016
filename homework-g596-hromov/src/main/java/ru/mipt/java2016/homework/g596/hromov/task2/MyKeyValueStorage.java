package ru.mipt.java2016.homework.g596.hromov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
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
    int numberOfChanges;


    private class MyIterator implements Iterator<Map.Entry<K, V>> {
            private Iterator<Map.Entry<K, V>> iterator;
            private int numberOfIteratorChanges;

            MyIterator() {
                iterator = dataBase.entrySet().iterator();
                numberOfIteratorChanges = numberOfChanges;
            };

            @Override
            public boolean hasNext() {
                if (numberOfIteratorChanges != numberOfChanges)
                    throw new ConcurrentModificationException();
                return iterator.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
                if (numberOfIteratorChanges != numberOfChanges)
                    throw new ConcurrentModificationException();
                return iterator.next();
            }

            @Override
            public void remove() {
                if (numberOfIteratorChanges != numberOfChanges)
                    throw new ConcurrentModificationException();
                delete(this.next().getKey());
                numberOfChanges++;
                numberOfIteratorChanges++;
            }
    }

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
        if (exists(key)) {
            return dataBase.get(key);
        } else
            return null;
    }

    @Override
    public void write(K key, V value) {
        dataBase.put(key, value);
        numberOfChanges++;
    }

    @Override
    public void delete(K key) {
        if (exists(key)) {
            dataBase.remove(key);
        }
        numberOfChanges++;
    }

    @Override
    public Iterator readKeys() throws ConcurrentModificationException {
        return new MyIterator();
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