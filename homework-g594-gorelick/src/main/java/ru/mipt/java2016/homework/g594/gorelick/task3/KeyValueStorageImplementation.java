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
    private HashMap<K, KeyPosition> fileMap;
    private final FileWorker<K> keyFileWorker;
    private final FileWorker<V> valueFileWorker;
    private static final int DATA_CAPACITY = 600;

    private class KeyPosition {
        private int id;
        private long position;

        KeyPosition(int id, long position) {
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

    KeyValueStorageImplementation(String path, FileWorker<K> keyS, FileWorker<V> valueS) throws IOException {
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
        keyFileWorker = keyS;
        valueFileWorker = valueS;
        databasePath = path;
        File database = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE);
        boolean isCreated = database.createNewFile();
        file = new RandomAccessFile(database, "rw");
        if (!isCreated) {
            IntegerFileWorker intS = new IntegerFileWorker();
            LLongFileWorker longS = new LLongFileWorker();
            int countFiles = intS.read(file, file.getFilePointer());
            int sizeKeys = intS.read(file, file.getFilePointer());
            for (int i = 0; i < sizeKeys; i++) {
                K key = keyFileWorker.read(file, file.getFilePointer());
                int id = intS.read(file, file.getFilePointer());
                long shift = longS.read(file, file.getFilePointer());

                setKeys.add(key);
                fileMap.put(key, new KeyPosition(id, shift));
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
                RandomAccessFile tmp = new RandomAccessFile(filesTable.get(id), "rw");
                V value = valueFileWorker.read(tmp, shift);
                tmp.close();
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
                    KeyPosition place = new KeyPosition(id, current.getFilePointer());
                    fileMap.put(entry.getKey(), place);
                    valueFileWorker.write(current, entry.getValue(), current.getFilePointer());
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
                KeyPosition place = new KeyPosition(id, current.getFilePointer());
                fileMap.put(entry.getKey(), place);
                valueFileWorker.write(current, entry.getValue(), current.getFilePointer());
            }
            valueMap.clear();
            current.close();
        } catch (IOException error) {
            error.printStackTrace();
        }
        file.setLength(0);
        file.seek(0);
        IntegerFileWorker integerFileWorker = new IntegerFileWorker();
        LLongFileWorker longFileWorker = new LLongFileWorker();
        integerFileWorker.write(file, filesTable.size(), file.getFilePointer());
        integerFileWorker.write(file, fileMap.size(), file.getFilePointer());
        for (Map.Entry<K, KeyPosition> entry : fileMap.entrySet()) {
            K key = entry.getKey();
            KeyPosition place = entry.getValue();
            keyFileWorker.write(file, key, file.getFilePointer());
            integerFileWorker.write(file, place.getId(), file.getFilePointer());
            longFileWorker.write(file, place.getPosition(), file.getFilePointer());
        }
        file.close();
        Files.delete(ifopen.toPath());
    }
}