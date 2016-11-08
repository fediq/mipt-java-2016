package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class KVStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private final FileWorker fileWorker;

    private final SerializerInterface<K> keySerializer;

    private final SerializerInterface<V> valueSerializer;

    private final String keyName;

    private final String valueName;

    private final HashMap<K, V> tempStorage = new HashMap<K, V>();

    private static final String VALIDATE_STRING = "itismyawesomestoragedontfakeit";

    private Boolean flag = false;

    public KVStorageImpl(String dirPath, SerializerInterface<K> keySerializer,
                         SerializerInterface<V> valueSerializer, String keyName, String valueName) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.keyName = keyName;
        this.valueName = valueName;
        fileWorker = new FileWorker(dirPath + "/mystorage.db");

        try {

            if (fileWorker.exists()) {
                if (!validateFile()) {
                    throw new RuntimeException("Invalid File");
                }
            }
        } catch (FileNotFoundException except) {
            fileWorker.createFile();
            flushTemp();
        }
    }

    private boolean validateFile() throws FileNotFoundException {
        String inputString = fileWorker.read();
        String[] tokens = inputString.split("\n");
        if (!tokens[0].equals(VALIDATE_STRING)) {
            return false;
        }
        if (!tokens[1].equals(keyName)) {
            return false;
        }
        if (!tokens[2].equals(valueName)) {
            return false;
        }
        int size = Integer.parseInt(tokens[3]);
        tempStorage.clear();
        for (int i = 0; i < size; ++i) {
            try {
                K str1 = keySerializer.deserialize(tokens[2 * i + 4]);
                V str2 = valueSerializer.deserialize(tokens[2 * i + 5]);
                tempStorage.put(str1, str2);
            } catch (StorageException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public V read(K key) {
        if (flag) {
            throw new RuntimeException("storage closed");
        }
        return tempStorage.get(key);
    }

    @Override
    public boolean exists(K key) {
        if (flag) {
            throw new RuntimeException("storage closed");
        }
        return tempStorage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (flag) {
            throw new RuntimeException("storage closed");
        }
        tempStorage.put(key, value);
        flushTemp();
    }

    @Override
    public void delete(K key) {
        if (flag) {
            throw new RuntimeException("storage closed");
        }
        tempStorage.remove(key);
        flushTemp();
    }

    @Override
    public Iterator<K> readKeys() {
        if (flag) {
            throw new RuntimeException("storage closed");
        }
        return tempStorage.keySet().iterator();
    }

    @Override
    public int size() {
        if (flag) {
            throw new RuntimeException("storage closed");
        }
        return tempStorage.size();
    }

    @Override
    public void close() {
        flag = true;
    }

    void flushTemp() {
        StringBuilder text = new StringBuilder(VALIDATE_STRING + "\n");
        text.append(keyName).append('\n').append(valueName).append('\n');
        text.append(tempStorage.size()).append('\n');
        for (Map.Entry<K, V> entry : tempStorage.entrySet()) {
            text.append(keySerializer.serialize(entry.getKey()))
                    .append('\n')
                    .append(valueSerializer.serialize(entry.getValue()))
                    .append('\n');
        }
        fileWorker.write(text.toString());
    }
}
