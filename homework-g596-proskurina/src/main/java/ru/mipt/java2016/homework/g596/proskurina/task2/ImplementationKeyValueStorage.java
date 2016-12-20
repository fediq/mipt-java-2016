package ru.mipt.java2016.homework.g596.proskurina.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class ImplementationKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final HashMap<K, V> map = new HashMap<>();

    private final String keyName;
    private final String valueName;

    private final SerialiserInterface<K> keySerialiser;
    private final SerialiserInterface<V> valueSerialiser;

    private final FileWorker file;
    private final String fileName;
    private static final String VALIDATION_STRING = "EtotFileZapisanMoeiProgoi";
    private boolean openFlag = true;


    public ImplementationKeyValueStorage(String keyName, String valueName,
                                         SerialiserInterface<K> keySerialiser, SerialiserInterface<V> valueSerialiser,
                                         String directoryPath) {

        this.keyName = keyName;
        this.valueName = valueName;

        this.keySerialiser = keySerialiser;
        this.valueSerialiser = valueSerialiser;

        file = new FileWorker();
        fileName = directoryPath + "/myFile.db";

        try {
            String inputData = file.read(fileName);

            String[] tokens = inputData.split("\n");
            if (!tokens[0].equals(VALIDATION_STRING)) {
                throw new RuntimeException("Not my file");
            }
            if (!tokens[1].equals(keyName)) {
                throw new RuntimeException("Wrong key type");
            }
            if (!tokens[2].equals(valueName)) {
                throw new RuntimeException("Wrong value type");
            }

            Integer objectsNumber = Integer.parseInt(tokens[3]);
            for (int i = 0; i < objectsNumber; ++i) {
                K key = keySerialiser.deserialise(tokens[2 * i + 4]);
                V value = valueSerialiser.deserialise(tokens[2 * i + 5]);
                map.put(key, value);
            }

        } catch (FileNotFoundException e) {
            writeData();
        }


    }

    @Override
    public V read(K key) {
        if (openFlag) {
            return map.get(key);
        } else {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (openFlag) {
            map.put(key, value);
        } else {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        if (openFlag) {
            return map.keySet().iterator();
        } else {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() {
        openFlag = false;
        writeData();
    }

    public void writeData() {
        StringBuffer text = new StringBuffer(VALIDATION_STRING + "\n");
        text.append(keyName).append('\n').append(valueName).append('\n');
        text.append(map.size()).append('\n');
        for (Map.Entry<K, V> entry : map.entrySet()) {
            text.append(keySerialiser.serialise(entry.getKey()))
                    .append('\n')
                    .append(valueSerialiser.serialise(entry.getValue()))
                    .append('\n');
        }
        file.write(fileName, text.toString());

    }

}