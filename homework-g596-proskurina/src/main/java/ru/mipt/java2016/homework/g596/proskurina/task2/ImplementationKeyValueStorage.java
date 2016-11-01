package ru.mipt.java2016.homework.g596.proskurina.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class ImplementationKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final Map<K, V> map = new HashMap<>();

    private final SerialiserInterface<K> keySerialiser;
    private final SerialiserInterface<V> valueSerialiser;

    private final FileWorker file;
    private final String fileName;
    private static final String VALIDATION_STRING = "EtotFileZapisanMoeiProgoi";
    private boolean openFlag = true;


    public ImplementationKeyValueStorage(String keyName, String valueName,
                                         SerialiserInterface<K> keySerialiser, SerialiserInterface<V> valueSerialiser,
                                         String directoryPath) {

        this.keySerialiser = keySerialiser;
        this.valueSerialiser = valueSerialiser;

        file = new FileWorker();
        fileName = directoryPath + File.separator + "myFile.db";

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

    private void checkIfFileIsOpen() {
        if (!openFlag) {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public V read(K key) {
        checkIfFileIsOpen();
        return map.get(key);

    }

    @Override
    public boolean exists(K key) {
        checkIfFileIsOpen();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkIfFileIsOpen();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkIfFileIsOpen();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkIfFileIsOpen();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        checkIfFileIsOpen();
        return map.size();
    }

    @Override
    public void close() {
        checkIfFileIsOpen();
        openFlag = false;
        writeData();
        map.clear();
    }

    public void writeData() {
        StringBuffer text = new StringBuffer(VALIDATION_STRING + "\n");
        text.append(keySerialiser.getType()).append('\n').append(valueSerialiser.getType()).append('\n');
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
