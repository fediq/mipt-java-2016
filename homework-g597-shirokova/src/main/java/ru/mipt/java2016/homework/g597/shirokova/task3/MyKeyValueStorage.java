package ru.mipt.java2016.homework.g597.shirokova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final int MAX_SIZE_OF_LATEST_DATA = 1300;

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;

    private Map<K, V> latestData = new HashMap<K, V>();
    private Set<K> setKeys = new HashSet<K>();
    private Set<placeOfValue> emptyPlace = new HashSet<>();
    private Map<K, placeOfValue> shiftTable = new HashMap<K, placeOfValue>();
    private Map<Integer, RandomAccessFile> listOfFiles = new HashMap<>();

    private ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();

    private String storageFileName;
    private String directoryName;
    private String emptyPlacesFileName;

    private boolean isClosed = false;

    private class placeOfValue {

        private int numberOfFile;
        private long shift;
        private long size;

        placeOfValue(int newFileNumber, long newShift, long newSize) {
            numberOfFile = newFileNumber;
            shift = newShift;
            size = newSize;
        }
    }

    MyKeyValueStorage(String currentPath, SerializationStrategy<K> serializerForKeys,
                      SerializationStrategy<V> serializerForValues) throws IOException {
        keySerializer = serializerForKeys;
        valueSerializer = serializerForValues;
        directoryName = currentPath;
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            storageFileName = directoryName + File.separator + "Storage";
            emptyPlacesFileName = directoryName + File.separator + "Empties";
            File storageFile = new File(storageFileName);
            if (!storageFile.createNewFile()) {
                readStorage();
            }
        } finally {
            lock.unlock();
        }
    }

    private void addEmpties(placeOfValue newEmptiness) {
        boolean mergedNext = false;
        boolean mergedPrev = false;
        for (placeOfValue location : emptyPlace) {
            if (newEmptiness.numberOfFile == location.numberOfFile) {
                if (newEmptiness.shift + newEmptiness.size == location.shift) {
                    placeOfValue mergedEmpties = new placeOfValue(newEmptiness.numberOfFile,
                            newEmptiness.shift, newEmptiness.size + location.size);
                    emptyPlace.add(mergedEmpties);
                    emptyPlace.remove(location);
                    mergedNext = true;
                    break;
                }
            }
        }
        for (placeOfValue location : emptyPlace) {
            if (newEmptiness.numberOfFile == location.numberOfFile) {
                if (newEmptiness.shift == location.shift + location.shift) {
                    placeOfValue mergedEmpties = new placeOfValue(newEmptiness.numberOfFile,
                            location.shift, newEmptiness.size + location.size);
                    emptyPlace.add(mergedEmpties);
                    emptyPlace.remove(location);
                    mergedPrev = true;
                    break;
                }
            }
        }
        if (!mergedNext && !mergedPrev) {
            emptyPlace.add(newEmptiness);
        }
    }

    private void readStorage() {
        try (DataInputStream storage = new DataInputStream(new BufferedInputStream(
                new FileInputStream(storageFileName)))) {
            File emptyPlaceFile = new File(emptyPlacesFileName);
            if (!emptyPlaceFile.createNewFile()) {
                try (DataInputStream empties = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(emptyPlacesFileName)))) {
                    placeOfValue location = new placeOfValue(empties.readInt(),
                            empties.readLong(), empties.readLong());
                    emptyPlace.add(location);
                }
            }
            int countOfFiles = storage.readInt();
            for (Integer i = 0; i < countOfFiles; ++i) {
                File currentFile = new File(directoryName + File.separator + i.toString());
                listOfFiles.put(i, new RandomAccessFile(currentFile, "rw"));
            }
            int countOfData = storage.readInt();
            for (int i = 0; i < countOfData; ++i) {
                K key = keySerializer.deserialize(storage);
                int fileNumber = storage.readInt();
                long shift = storage.readLong();
                long size = storage.readLong();
                shiftTable.put(key, new placeOfValue(fileNumber, shift, size));
                setKeys.add(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLatestData() throws IOException {
        RandomAccessFile currentFile;
        Integer numberOfNewFile = listOfFiles.size();
        String newFileName = directoryName + File.separator + numberOfNewFile.toString();
        listOfFiles.put(numberOfNewFile, new RandomAccessFile(newFileName, "rw"));
        for (Map.Entry<K, V> entry : latestData.entrySet()) {
            if (entry.getValue().equals(null)) {
                shiftTable.remove(entry.getKey());
                continue;
            }
            if (shiftTable.containsKey(entry.getKey())) {
                addEmpties(shiftTable.get(entry.getKey()));
            }
            currentFile = listOfFiles.get(numberOfNewFile);
            currentFile.seek(currentFile.length());
            long start = currentFile.getFilePointer();
            valueSerializer.serialize(currentFile, entry.getValue());
            long finish = currentFile.getFilePointer();
            placeOfValue newLocation = new placeOfValue(numberOfNewFile, start, finish - start);
            shiftTable.put(entry.getKey(), newLocation);
            for (placeOfValue location : emptyPlace) {
                if (location.size == finish - start) {
                    shiftTable.put(entry.getKey(), location);
                    currentFile = listOfFiles.get(location.numberOfFile);
                    currentFile.seek(location.shift);
                    valueSerializer.serialize(currentFile, entry.getValue());
                    emptyPlace.remove(location);
                    addEmpties(newLocation);
                    continue;
                }
                if (location.size > finish - start) {
                    shiftTable.put(entry.getKey(), location);
                    currentFile = listOfFiles.get(location.numberOfFile);
                    currentFile.seek(location.shift);
                    valueSerializer.serialize(currentFile, entry.getValue());
                    placeOfValue restOfEmptiness = new placeOfValue(location.numberOfFile,
                            location.shift + finish - start,
                            location.size - finish + start);
                    emptyPlace.remove(location);
                    emptyPlace.add(restOfEmptiness);
                    addEmpties(newLocation);
                }
            }
        }
        latestData.clear();
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("File is closed");
        }
    }

    @Override
    public V read(K key) {
        checkClosed();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            if (latestData.keySet().contains(key)) {
                return latestData.get(key);
            }
            if (shiftTable.keySet().contains(key)) {
                int fileNumber = shiftTable.get(key).numberOfFile;
                long shift = shiftTable.get(key).shift;
                RandomAccessFile currentFile = listOfFiles.get(fileNumber);
                try {
                    currentFile.seek(shift);
                    return valueSerializer.deserialize(currentFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        checkClosed();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            return setKeys.contains(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        checkClosed();
        Lock writeLock = globalLock.writeLock();
        Lock readLock = globalLock.readLock();
        writeLock.lock();
        readLock.lock();
        try {
            latestData.put(key, value);
            setKeys.add(key);
            if (latestData.size() >= MAX_SIZE_OF_LATEST_DATA) {
                writeLatestData();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            writeLock.unlock();
            readLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        checkClosed();
        Lock writeLock = globalLock.writeLock();
        Lock readLock = globalLock.readLock();
        writeLock.lock();
        readLock.lock();
        try {
            setKeys.remove(key);
            if (!latestData.containsKey(key)) {
                addEmpties(shiftTable.get(key));
                shiftTable.remove(key);
            }
        } finally {
            writeLock.unlock();
            readLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            return setKeys.iterator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        checkClosed();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            return setKeys.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        writeLatestData();
        if (!emptyPlace.isEmpty()) {
            try (DataOutputStream empties = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(emptyPlacesFileName)))) {
                for (placeOfValue location : emptyPlace) {
                    empties.writeInt(location.numberOfFile);
                    empties.writeLong(location.shift);
                    empties.writeLong(location.size);
                }
            }
        } else {
            File empties = new File(emptyPlacesFileName);
            empties.delete();
        }
        try (DataOutputStream storage = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(storageFileName)))) {
            storage.writeInt(listOfFiles.size());
            storage.writeInt(shiftTable.size());
            for (Map.Entry<K, placeOfValue> entry : shiftTable.entrySet()) {
                keySerializer.serialize(storage, entry.getKey());
                storage.writeInt(entry.getValue().numberOfFile);
                storage.writeLong(entry.getValue().shift);
                storage.writeLong(entry.getValue().size);
            }
            for (Map.Entry<Integer, RandomAccessFile> currentFile : listOfFiles.entrySet()) {
                currentFile.getValue().close();
            }
        }
        latestData = null;
        isClosed = true;
    }

}