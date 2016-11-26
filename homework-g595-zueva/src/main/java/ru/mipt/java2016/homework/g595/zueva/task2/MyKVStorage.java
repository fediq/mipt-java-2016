package ru.mipt.java2016.homework.g595.zueva.task2;

/*сreated by nestyme*/

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.util.Iterator;
import java.util.Map;
import java.io.*;
import java.util.HashMap;


public class MyKVStorage<K, V>
        implements KeyValueStorage<K, V> {
    private static final String VALIDATION_STRING = "My strange storage";


    private String filename;
    private HashMap<K, V> currentstorage;
    private SerializerStorage<K> keySerializerStorage;
    private SerializerStorage<V> valueSerializerStorage;
    private boolean ifOpen = false;

    /*Конструктор класса хранилища*/

    public MyKVStorage(String newFileName,
                       SerializerStorage newKeySerialisation,
                       SerializerStorage newValueSerialisation) throws IOException, Exception {
        keySerializerStorage = newKeySerialisation;
        valueSerializerStorage = newValueSerialisation;
        filename = newFileName + "/note.txt";
        currentstorage = new HashMap<K, V>();
        ifOpen = true;

        /*проверка, возможно ли открыть и создать файл*/

        File destination = new File(filename);
        if (!destination.exists()) {
            destination.createNewFile();

            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))) {

                out.writeUTF(VALIDATION_STRING);
                out.writeInt(0b0);

            }
        }
        /* Существует ли наша валидирующая строка в файле &&
        проверка, возможно ли читать из файла*/
        try (DataInputStream in = new DataInputStream(new FileInputStream(filename))) {
            if (!in.readUTF().equals(VALIDATION_STRING)) {
                throw new IllegalStateException("Unknown validation");
            }
            int quantity = in.readInt();
            for (int i = 0; i < quantity; ++i) {

                K keyToInsert = keySerializerStorage.readFromStream(in);
                V valueToInsert = valueSerializerStorage.readFromStream(in);
                currentstorage.put(keyToInsert, valueToInsert);
            }
        }
    }

    @Override
    public void close() throws IOException {
        isFileClosed();
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))) {
            out.writeUTF(VALIDATION_STRING);
            out.writeInt(currentstorage.size());
            for (Map.Entry<K, V> i : currentstorage.entrySet()) {

                keySerializerStorage.writeToStream(out, i.getKey());
                valueSerializerStorage.writeToStream(out, i.getValue());

            }
            ifOpen = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V read(K key) {
        isFileClosed();
        return currentstorage.get(key);
    }

    @Override
    public boolean exists(K key) {
        isFileClosed();
        return currentstorage.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        isFileClosed();
        currentstorage.put(key, value);
    }

    @Override
    public void delete(K key) {
        isFileClosed();
        currentstorage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        isFileClosed();
        return currentstorage.keySet().iterator();
    }

    @Override
    public int size() {
        isFileClosed();
        return currentstorage.size();
    }


    public void isFileClosed() {
        if (!ifOpen) {
            throw new IllegalStateException("Storage had been already closed");
        }
    }
}

