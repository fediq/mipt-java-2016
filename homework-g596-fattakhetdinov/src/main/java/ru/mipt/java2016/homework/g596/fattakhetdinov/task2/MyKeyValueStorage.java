package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private Map<K, V> database = new HashMap<>();

    private File databaseFile; //Файл для хранения БД
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private final String currentStorageType; //Строка для проверки типа хранилища
    private boolean isClosed;

    public MyKeyValueStorage(String path, SerializationStrategy<K> keySerializationStrategy,
            SerializationStrategy<V> valueSerializationStrategy) throws IOException {
        currentStorageType =
                keySerializationStrategy.getType() + " : " + valueSerializationStrategy.getType();
        isClosed = false;

        File directory = new File(path);
        //Проверяем переданный путь на корректность
        if (!directory.isDirectory() || !directory.exists()) {
            throw new RuntimeException("Path doesn't exist");
        }

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        databaseFile = new File(path + File.separator + "storage.db");
        if (databaseFile.exists()) {
            loadDataFromFile(); //Если файл существует, подгружаем данные из него
        }
    }

    private void loadDataFromFile() throws IOException {
        try (DataInputStream input = new DataInputStream(new FileInputStream(databaseFile))) {
            String fileStorageType = input.readUTF(); //Считываем проверочную строку
            if (!currentStorageType.equals(fileStorageType)) {
                throw new RuntimeException(
                        String.format("Storage file contains: %s; expected: %s", fileStorageType,
                                currentStorageType));
            }
            int numValues = input.readInt(); //Размер базы данных
            //Считываем из файла, используя переданные в конструктор стратегии сериализации
            for (int i = 0; i < numValues; i++) {
                K key = keySerializationStrategy.deserializeFromFile(input);
                V value = valueSerializationStrategy.deserializeFromFile(input);
                database.put(key, value);
            }
        } catch (IOException e) {
            throw new IOException("Can't read from file " + databaseFile.getPath(), e);
        }
    }

    private void checkForClosedDatabaseFile() {
        //Если файл закрыли, и кто-то пытается сделать что-то с БД, то кидаем исключение
        if (isClosed) {
            throw new RuntimeException("Access to the closed file");
        }
    }

    @Override
    public V read(K key) {
        checkForClosedDatabaseFile();
        return database.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkForClosedDatabaseFile();
        return database.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkForClosedDatabaseFile();
        database.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkForClosedDatabaseFile();
        database.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkForClosedDatabaseFile();
        return database.keySet().iterator();
    }

    @Override
    public int size() {
        checkForClosedDatabaseFile();
        return database.size();
    }

    @Override
    public void close() throws IOException {
        checkForClosedDatabaseFile();
        isClosed = true;
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(databaseFile))) {
            output.writeUTF(currentStorageType); //Пишем проверочную строку
            output.writeInt(database.size()); //Размер базы данных
            for (Map.Entry<K, V> entry : database.entrySet()) {
                keySerializationStrategy.serializeToFile(entry.getKey(), output);
                valueSerializationStrategy.serializeToFile(entry.getValue(), output);
            }
            database = null;
        } catch (IOException e) {
            throw new IOException("Can't write to " + databaseFile.getPath(), e);
        }

    }
}