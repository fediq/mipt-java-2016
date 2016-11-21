package ru.mipt.java2016.homework.g594.gorelick.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;

public class KeyValueStorageImplementation<K, V> implements KeyValueStorage<K, V> {
    private static final String DATABASE_NAME_TEMPLATE = "storage.db";
    private static String databasePath;
    private RandomAccessFile file;
    private File ifopen;
    private ArrayList<File> filesTable;
    private HashSet<K> setKeys;
    private HashMap<K, V> valueMap;
    private HashMap<K, keyPlace> fileMap;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private static final int DATA_CAPACITY = 250;

    private class keyPlace {
        private int id;
        private long position;

        keyPlace(int id, long position) {
            this.id = id;
            this.position = position;
        }

        public int getId() {
            return this.id;
        }

        public long getPosition() {
            return this.position;
        }
    }

    KeyValueStorageImplementation(String path, Serializer<K> keyS, Serializer<V> valueS) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new IOException("Wrong path");
        }
        ifopen = new File(path + File.separator + DATABASE_NAME_TEMPLATE + ".check");
        if (!ifopen.createNewFile()) {
            throw new IOException("Database is already opened.");
        }
        setKeys = new HashSet<>();
        valueMap = new HashMap<>();
        fileMap = new HashMap<>();
        filesTable = new ArrayList<>();
        keySerializer = keyS;
        valueSerializer = valueS;
        databasePath = path;
        File database = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE);
        boolean isCreated = database.createNewFile();
        file = new RandomAccessFile(database, "rw");
        if (!isCreated) {
            IntegerSerializer intS = new IntegerSerializer();
            LLongSerializer longS = new LLongSerializer();
            int countFiles = intS.read(file, file.getFilePointer());
            int sizeKeys = intS.read(file, file.getFilePointer());
            for (int i = 0; i < sizeKeys; i++) {
                K key = keySerializer.read(file, file.getFilePointer());
                int id = intS.read(file, file.getFilePointer());
                long shift = longS.read(file, file.getFilePointer());

                setKeys.add(key);
                fileMap.put(key, new keyPlace(id, shift));
            }

            for (int i = 0; i < countFiles; i++) {
                File currentFile = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + i);
                currentFile.createNewFile();
                filesTable.add(currentFile);
            }
        }
    }

    @Override
    public V read(K key) {
        if (valueMap.keySet().contains(key)) {
            return valueMap.get(key);
        } else if (fileMap.containsKey(key)) {
            int id = fileMap.get(key).getId();
            long shift = fileMap.get(key).getPosition();
            try {
                RandomAccessFile file = new RandomAccessFile(filesTable.get(id), "rw");
                V value = valueSerializer.read(file, shift);
                file.close();
                return value;
            } catch (IOException error) {
                error.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void write(K key, V value) {
        setKeys.add(key);
        valueMap.put(key, value);
        if (valueMap.size() >= DATA_CAPACITY) {
            int id = filesTable.size();
            File tmp = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + id);
            try {
                tmp.createNewFile();
                filesTable.add(tmp);
                RandomAccessFile current = new RandomAccessFile(tmp, "rw");
                current.setLength(0);
                current.seek(0);
                for (Map.Entry<K, V> entry : valueMap.entrySet()) {
                    keyPlace place = new keyPlace(id, current.getFilePointer());
                    fileMap.put(entry.getKey(), place);
                    valueSerializer.write(current, entry.getValue(), current.getFilePointer());
                }
                valueMap.clear();
                current.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }


    @Override
    public boolean exists(K key) {
        return setKeys.contains(key);
    }

    @Override
    public void delete(K key) {
        setKeys.remove(key);
        fileMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return setKeys.iterator();
    }

    @Override
    public int size() {
        return setKeys.size();
    }

    @Override
    public void close() throws IOException {
        int id = filesTable.size();
        File tmp = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + id);
        try {
            tmp.createNewFile();
            filesTable.add(tmp);
            RandomAccessFile current = new RandomAccessFile(tmp, "rw");
            current.setLength(0);
            current.seek(0);
            for (Map.Entry<K, V> entry : valueMap.entrySet()) {
                keyPlace place = new keyPlace(id, current.getFilePointer());
                fileMap.put(entry.getKey(), place);
                valueSerializer.write(current, entry.getValue(), current.getFilePointer());
            }
            valueMap.clear();
            current.close();
        } catch (IOException error) {
            error.printStackTrace();
        }
        file.setLength(0);
        file.seek(0);
        IntegerSerializer integerSerializer = new IntegerSerializer();
        LLongSerializer longSerializer = new LLongSerializer();
        integerSerializer.write(file, filesTable.size(), file.getFilePointer());
        integerSerializer.write(file, fileMap.size(), file.getFilePointer());
        for (Map.Entry<K, keyPlace> entry : fileMap.entrySet()) {
            K key = entry.getKey();
            keyPlace place = entry.getValue();
            keySerializer.write(file, key, file.getFilePointer());
            integerSerializer.write(file, place.getId(), file.getFilePointer());
            longSerializer.write(file, place.getPosition(), file.getFilePointer());
        }
        file.close();
        Files.delete(ifopen.toPath());
    }
}