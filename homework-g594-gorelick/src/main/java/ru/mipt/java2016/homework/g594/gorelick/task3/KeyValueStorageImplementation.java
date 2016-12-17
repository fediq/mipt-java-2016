package ru.mipt.java2016.homework.g594.gorelick.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.io.IOException;

public class KeyValueStorageImplementation<K, V> implements KeyValueStorage<K, V> {
    private static final String DATABASE_NAME_TEMPLATE = "storage.db";
    private static String databasePath;
    private RandomAccessFile file;
    private File lockfile;
    private ArrayList<File> filesTable;
    private HashSet<K> setKeys;
    private HashMap<K, V> Cache;
    private HashMap<K, KeyPosition> fileMap;
    private HashMap<K, KeyPosition> deletedMap;
    private final FileWorker<K> keyFileWorker;
    private final FileWorker<V> valueFileWorker;
    private static final int DATA_CAPACITY = 300;

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

    private synchronized void touchDB() throws IllegalStateException {
        if (!lockfile.exists())
            throw new IllegalStateException("Working with closed DB");
    }

    KeyValueStorageImplementation(String path, FileWorker<K> kFileWorker, FileWorker<V> vFileWorker) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new IOException("Wrong path");
        }
        lockfile = new File(path + File.separator + DATABASE_NAME_TEMPLATE + ".lock");
        if (!lockfile.createNewFile()) {
            throw new IOException("Database is already opened.");
        }
        setKeys = new HashSet<>();
        Cache = new HashMap<>();
        fileMap = new HashMap<>();
        deletedMap = new HashMap<>();
        filesTable = new ArrayList<>();
        keyFileWorker = kFileWorker;
        valueFileWorker = vFileWorker;
        databasePath = path;
        File database = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE);
        boolean fileCreated = database.createNewFile();
        file = new RandomAccessFile(database, "rw");
        if (!fileCreated) {
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

    private synchronized void deleteHoles() throws IOException {
        try {
            for (int id = 0; id < filesTable.size() - 1; id++) {
                int main_result = id;
                int rest_result = id + 1;
                HashMap<K, KeyPosition> newMapKeyFile = new HashMap<>();
                File file1 = filesTable.get(main_result);
                File file2 = filesTable.get(rest_result);
                RandomAccessFile RAF1 = new RandomAccessFile(file1, "rw");
                RandomAccessFile RAF2 = new RandomAccessFile(file2, "rw");
                int currentKeys = 0;
                long currentShift = 0;
                File currentFile = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + "new1");
                currentFile.createNewFile();
                RandomAccessFile currentFileRAM = new RandomAccessFile(currentFile, "rw");

                while (currentShift != RAF1.length()) {
                    K key = keyFileWorker.read(RAF1, RAF1.getFilePointer());
                    long currentKeyShift = RAF1.getFilePointer();
                    V value = valueFileWorker.read(RAF1, RAF1.getFilePointer());
                    currentShift = RAF1.getFilePointer();
                    if (deletedMap.containsKey(key)) {
                        KeyPosition place = deletedMap.get(key);
                        if (place.getId() == main_result && place.getPosition() == currentKeyShift) {
                            continue;
                        }
                    }
                    keyFileWorker.write(currentFileRAM, key, currentFileRAM.getFilePointer());
                    newMapKeyFile.put(key, new KeyPosition(main_result, currentFileRAM.getFilePointer()));
                    valueFileWorker.write(currentFileRAM, value, currentFileRAM.getFilePointer());
                    currentKeys++;
                }
                currentShift = 0;
                while (currentShift != RAF2.length() && currentKeys < DATA_CAPACITY) {
                    K key = keyFileWorker.read(RAF2, RAF2.getFilePointer());
                    long currentKeyShift = RAF2.getFilePointer();
                    V value = valueFileWorker.read(RAF2, RAF2.getFilePointer());
                    currentShift = RAF2.getFilePointer();
                    if (deletedMap.containsKey(key)) {
                        KeyPosition place = deletedMap.get(key);
                        if (place.getId() == rest_result && place.getPosition() == currentKeyShift) {
                            continue;
                        }
                    }
                    keyFileWorker.write(currentFileRAM, key, currentFileRAM.getFilePointer());
                    newMapKeyFile.put(key, new KeyPosition(rest_result, currentFileRAM.getFilePointer()));
                    valueFileWorker.write(currentFileRAM, value, currentFileRAM.getFilePointer());
                    currentKeys++;
                }

                RAF1.close();
                Files.delete(filesTable.get(id).toPath());
                currentFileRAM.close();
                currentFile.renameTo(file1);

                if (currentKeys >= DATA_CAPACITY) {
                    currentFile = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + "new2");
                    currentFile.createNewFile();
                    currentFileRAM = new RandomAccessFile(currentFile, "rw");

                    while (currentShift != RAF2.length()) {
                        K key = keyFileWorker.read(RAF2, RAF2.getFilePointer());
                        long currentKeyShift = RAF2.getFilePointer();
                        V value = valueFileWorker.read(RAF2, RAF2.getFilePointer());
                        currentShift = RAF2.getFilePointer();
                        if (deletedMap.containsKey(key)) {
                            KeyPosition place = deletedMap.get(key);
                            if (place.getId() == rest_result && place.getPosition() == currentKeyShift) {
                                continue;
                            }
                        }
                        keyFileWorker.write(currentFileRAM, key, currentFileRAM.getFilePointer());
                        newMapKeyFile.put(key, new KeyPosition(rest_result, currentFileRAM.getFilePointer()));
                        valueFileWorker.write(currentFileRAM, value, currentFileRAM.getFilePointer());
                    }
                }

                RAF2.close();
                Files.delete(filesTable.get(id + 1).toPath());
                if (currentKeys >= DATA_CAPACITY) {
                    currentFileRAM.close();
                    currentFile.renameTo(file2);
                } else {
                    for (int index = rest_result + 1; index < filesTable.size(); index++) {
                        int oldid = index;
                        int newid = index - 1;
                        File oldfile = filesTable.get(oldid);

                        for (Map.Entry<K, KeyPosition> entry : newMapKeyFile.entrySet()) {
                            if (entry.getValue().getId() == oldid) {
                                entry.setValue(new KeyPosition(newid, entry.getValue().getPosition()));
                            }
                        }

                        File newfile = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + newid);
                        oldfile.renameTo(newfile);
                    }
                }

                for (Map.Entry<K, KeyPosition> entry : newMapKeyFile.entrySet()) {
                    setKeys.remove(entry.getKey());
                    fileMap.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    private synchronized void newPart() throws IOException {
        if (deletedMap.size() >= DATA_CAPACITY) {
            deleteHoles();
            deletedMap.clear();
        }
        if (Cache.size() >= DATA_CAPACITY)  {
            int id = filesTable.size();
            File tmp = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + id);
            try {
                tmp.createNewFile();
                filesTable.add(tmp);
                RandomAccessFile current = new RandomAccessFile(tmp, "rw");
                current.setLength(0);
                current.seek(0);
                for (Map.Entry<K, V> entry : Cache.entrySet()) {
                    KeyPosition place = new KeyPosition(id, current.getFilePointer());
                    fileMap.put(entry.getKey(), place);
                    valueFileWorker.write(current, entry.getValue(), current.getFilePointer());
                }
                Cache.clear();
                current.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }

    }

    @Override
    public synchronized V read(K key)  {
        touchDB();
        if (Cache.keySet().contains(key)) {
            return Cache.get(key);
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
    public synchronized void write(K key, V value) {
        touchDB();
        setKeys.add(key);
        Cache.put(key, value);
        try{
            newPart();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized boolean exists(K key) {
        return setKeys.contains(key);
    }

    @Override
    public synchronized void delete(K key) {
        touchDB();
        if (Cache.containsKey(key) || fileMap.containsKey(key))
            deletedMap.put(key, fileMap.get(key));
        setKeys.remove(key);
        Cache.remove(key);
        fileMap.remove(key);
        try {
            newPart();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        return setKeys.iterator();
    }

    @Override
    public synchronized int size() {
        return setKeys.size();
    }

    @Override
    public synchronized void close() throws IOException {
        if (!lockfile.exists())
            throw new IOException("Working with closed DB") ;
        if (deletedMap.size() > 0) {
            deleteHoles();
            deletedMap.clear();
        }
        if (Cache.size() > 0) {
            int id = filesTable.size();
            File tmp = new File(databasePath + File.separator + DATABASE_NAME_TEMPLATE + "." + id);
            try {
                tmp.createNewFile();
                filesTable.add(tmp);
                RandomAccessFile current = new RandomAccessFile(tmp, "rw");
                current.setLength(0);
                current.seek(0);
                for (Map.Entry<K, V> entry : Cache.entrySet()) {
                    KeyPosition place = new KeyPosition(id, current.getFilePointer());
                    fileMap.put(entry.getKey(), place);
                    valueFileWorker.write(current, entry.getValue(), current.getFilePointer());
                }
                Cache.clear();
                current.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
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
        Files.delete(lockfile.toPath());
    }
}