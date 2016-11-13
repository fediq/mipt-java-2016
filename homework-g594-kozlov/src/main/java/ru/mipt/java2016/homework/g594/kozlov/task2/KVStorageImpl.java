package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class KVStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private final FileWorker fileWorker;

    private final SerializerInterface<K> keySerializer;

    private final SerializerInterface<V> valueSerializer;

    private final Map<K, ValueWrapper> tempStorage = new HashMap<K, ValueWrapper>();

    private final Set<K> tempKeySet = new TreeSet<K>();

    private final Set<K> changedKeySet = new TreeSet<K>();

    private static final String VALIDATE_STRING = "itismyawesomestoragedontfakeit";

    private Boolean isClosedFlag = false;

    private class ValueWrapper {
        ValueWrapper(int st, V obj) {
            state = st;
            object = obj;
        }
        int state = 0; //0 for not loaded, 1 loaded, 2 new kv pair, 3 value was changed, 4 value was deleted
        V object = null;
    }

    public KVStorageImpl(String dirPath, SerializerInterface<K> keySerializer,
                         SerializerInterface<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        fileWorker = new FileWorker(dirPath + File.pathSeparator + "mystorage.db");

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
        if (!tokens[1].equals(keySerializer.getClassString())) {
            return false;
        }
        if (!tokens[2].equals(valueSerializer.getClassString())) {
            return false;
        }
        int size = Integer.parseInt(tokens[3]);
        tempStorage.clear();
        for (int i = 0; i < size; ++i) {
            try {
                K str1 = keySerializer.deserialize(tokens[2 * i + 4]);
                V str2 = valueSerializer.deserialize(tokens[2 * i + 5]);
                tempStorage.put(str1, new ValueWrapper(1, str2));
                tempKeySet.add(str1);
            } catch (StorageException e) {
                return false;
            }
        }
        return true;
    }

    void isClosed() {
        if (isClosedFlag) {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public V read(K key) {
        isClosed();
        ValueWrapper value = tempStorage.get(key);
        if (value == null) {
            return null;
        }
        if (value.state == 0) {
            loadKey(key);
            value = tempStorage.get(key);
        }
        return value.object;
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        ValueWrapper value = tempStorage.get(key);
        if (value == null || value.state == 4) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        ValueWrapper val = tempStorage.get(key);
        if (val == null) {
            tempStorage.put(key, new ValueWrapper(2, value));
            changedKeySet.add(key);
        } else {
            tempStorage.remove(key);
            val.state = 3;
            val.object = value;
            tempStorage.put(key, val);
            changedKeySet.add(key);
        }
        flushTemp();
    }

    @Override
    public void delete(K key) {
        isClosed();
        ValueWrapper value = tempStorage.get(key);
        tempStorage.remove(key);
        value.state = 4;
        value.object = null;
        tempStorage.put(key, value);
        changedKeySet.add(key);
        flushTemp();
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return tempStorage.keySet().iterator();
    }

    @Override
    public int size() {
        isClosed();
        return tempStorage.size();
    }

    @Override
    public void close() {
        isClosedFlag = true;
    }

    private void loadKey(K key) {}

    private void writeSysInfo() {

    }

    private void newFlushTemp() {
        writeSysInfo();
        int firstOffset = 0;
        for (K key: changedKeySet) {
            firstOffset += 8 + keySerializer.serialize(key).length();
        }




    }

    private void flushTemp() {
        StringBuilder text = new StringBuilder(VALIDATE_STRING + "\n");
        text.append(keySerializer.getClassString()).append('\n')
                .append(valueSerializer.getClassString()).append('\n');
        text.append(tempStorage.size()).append('\n');
        for (Map.Entry<K, ValueWrapper> entry : tempStorage.entrySet()) {
            text.append(keySerializer.serialize(entry.getKey()))
                    .append('\n')
                    .append(valueSerializer.serialize(entry.getValue().object))
                    .append('\n');
        }
        fileWorker.write(text.toString());
    }
}
