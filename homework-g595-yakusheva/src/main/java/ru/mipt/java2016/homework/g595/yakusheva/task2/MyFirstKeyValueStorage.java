package ru.mipt.java2016.homework.g595.yakusheva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Софья on 26.10.2016.
 */
public class MyFirstKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final Map<K, V> map;
    private boolean isClosedFlag;
    private MyFirstSerializerInterface<K> keySerializer;
    private MyFirstSerializerInterface<V> valueSerializer;
    private RandomAccessFile file;
    private int count;
    private String checkString = " this is our wonderful KeyValueStorage ";

    public MyFirstKeyValueStorage(String path, MyFirstSerializerInterface<K> newKeySerializerArg,
                                  MyFirstSerializerInterface<V> newValueSerializerArg) {
        boolean readFromOldFileFlag = false;
        keySerializer = newKeySerializerArg;
        valueSerializer = newValueSerializerArg;
        File f = new File(Paths.get(path, "storage.db").toString());
        if (!f.exists()) {
            try {
                f.createNewFile();
                readFromOldFileFlag = true;
            } catch (IOException e) {
                throw new RuntimeException("error: can't create new file");
            }
        }
        map = new HashMap<K, V>();
        try {
            file = new RandomAccessFile(f.getPath(), "rw");
        } catch (IOException e) {
            throw new RuntimeException("error: can't create new RandomAccessFile");
        }
        if (!readFromOldFileFlag) {
            isClosedFlag = false;
            readFromFile();
        }
    }

    private void closedCheck() {
        if (isClosedFlag) {
            throw new RuntimeException("error: can't write to closed file");
        }
    }

    @Override
    public V read(K key) {
        closedCheck();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        closedCheck();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        closedCheck();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        closedCheck();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        closedCheck();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        closedCheck();
        writeToFile();
        file.close();
        isClosedFlag = true;
    }

    private void readFromFile() {
        DataInputStream dataInputStream = new DataInputStream(Channels.newInputStream(file.getChannel()));
        K newKey;
        V newValue;
        try {
            String controlString = new String(file.readUTF());
            if (!controlString.equals(checkString)) {
                throw new RuntimeException("error: file is not right");
            }
        } catch (IOException e) {
            throw new RuntimeException("error: can't read file size from file");
        }
        try {
            count = file.readInt();
        } catch (IOException e) {
            throw new RuntimeException("error: can't read file size from file");
        }
        for (int i = 0; i < count; i++) {
            try {
                newKey = keySerializer.deserializeFromStream(dataInputStream);
                newValue = valueSerializer.deserializeFromStream(dataInputStream);
                map.put(newKey, newValue);
            } catch (IOException e) {
                throw new RuntimeException("error: can't read note from file");
            }
        }
    }

    private void writeToFile() throws IOException {
        file.seek(0);
        DataOutputStream dataOutputStream = new DataOutputStream(Channels.newOutputStream(file.getChannel()));
        try {
            file.writeUTF(checkString);
        } catch (IOException e) {
            throw new RuntimeException("error: can't write control string to file");
        }
        try {
            count = map.size();
            file.writeInt(count);
        } catch (IOException e) {
            throw new RuntimeException("error: can't write file size to file");
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            try {
                keySerializer.serializeToStream(dataOutputStream, entry.getKey());
                valueSerializer.serializeToStream(dataOutputStream, entry.getValue());
            } catch (IOException e) {
                throw new RuntimeException("error: can't write note to file");
            }
        }
    }
}
