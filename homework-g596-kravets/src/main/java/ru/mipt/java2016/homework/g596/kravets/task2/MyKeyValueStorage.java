package ru.mipt.java2016.homework.g596.kravets.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author Alena Kravets
 * @since 30.10.16
 */

public class MyKeyValueStorage<Key, Value> implements KeyValueStorage<Key, Value> {
    private MySerialization<Key> keySerializator;
    private MySerialization<Value> valueSerializator;
    private String fileTitle;
    private String keyType;
    private boolean isOpenedFile;
    private Map<Key, Value> actualMap = new HashMap<Key, Value>();

    public MyKeyValueStorage(String path, String type, MySerialization key, MySerialization value) {
        fileTitle = path + File.separator + "storage.db";
        keyType = type;
        keySerializator = key;
        valueSerializator = value;
        actualMap = new HashMap<Key, Value>();
        isOpenedFile = true;
        File file = new File(fileTitle);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Error! Couldn't create a new file");
            }
            try (DataOutputStream output = new DataOutputStream(new FileOutputStream(fileTitle))) {
                output.writeUTF(type);
                output.writeInt(0);
            } catch (IOException e) {
                throw new IllegalStateException("Error! Couldn't write a file.");
            }
        }
        try (DataInputStream input = new DataInputStream(new FileInputStream(fileTitle))) {
            if (!input.readUTF().equals(type)) {
                throw new RuntimeException("Error! Invalid storage format.");
            }
            int num = input.readInt();
            for (int i = 0; i < num; i++) {
                Key rKey = keySerializator.read(input);
                Value rValue = valueSerializator.read(input);
                actualMap.put(rKey, rValue);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error! Invalid storage format.");
        }
    }

    private void checkToOpenFile() {
        if (!isOpenedFile) {
            throw new RuntimeException("Error! You can't use closed storage!");
        }
    }

    @Override
    public Value read(Key key) {
        checkToOpenFile();
        return actualMap.get(key);
    }

    @Override
    public boolean exists(Key key) {
        checkToOpenFile();
        return actualMap.containsKey(key);
    }

    @Override
    public void write(Key key, Value value) {
        checkToOpenFile();
        actualMap.put(key, value);
    }

    @Override
    public void delete(Key key) {
        checkToOpenFile();
        actualMap.remove(key);
    }

    @Override
    public Iterator readKeys() {
        checkToOpenFile();
        return actualMap.keySet().iterator();
    }

    @Override
    public int size() {
        checkToOpenFile();
        return actualMap.size();
    }

    @Override
    public void close() throws IOException {
        checkToOpenFile();
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(fileTitle))) {
            output.writeUTF(keyType);
            output.writeInt(actualMap.size());
            for (Map.Entry<Key, Value> entry : actualMap.entrySet()) {
                keySerializator.write(output, entry.getKey());
                valueSerializator.write(output, entry.getValue());
            }
            isOpenedFile = false;
            actualMap.clear();
        } catch (IOException e) {
            throw new IOException("Error! Couldn't save file to storage.");
        }
    }
}



