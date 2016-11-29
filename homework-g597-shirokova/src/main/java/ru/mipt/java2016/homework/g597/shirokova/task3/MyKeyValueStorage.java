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
    private ArrayList<Map<Long, Long>> startsOfFreePlaces = new ArrayList<>();
    private ArrayList<Map<Long, Long>> endsOfFreePlaces = new ArrayList<>();
    private Map<K, PlaceOfValue> shiftTable = new HashMap<K, PlaceOfValue>();
    private Map<Integer, RandomAccessFile> listOfFiles = new HashMap<>();

    private ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();

    private String storageFileName;
    private String directoryName;
    private String emptyPlacesFileName;

    private boolean isClosed = false;

    static private class PlaceOfValue {

        private int numberOfFile;
        private long shift;
        private long size;

        PlaceOfValue(int newFileNumber, long newShift, long newSize) {
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
        storageFileName = directoryName + File.separator + "Storage";
        emptyPlacesFileName = directoryName + File.separator + "Empties";
        File storageFile = new File(storageFileName);
        if (!storageFile.createNewFile()) {
            readStorage();
        }
    }

    private void deallocate(PlaceOfValue newEmptiness) {
        boolean mergedNext = false;
        boolean mergedPrev = false;
        Integer numberOfFile = newEmptiness.numberOfFile;
        for (Map.Entry<Long, Long> location : startsOfFreePlaces.get(numberOfFile).entrySet()) {
            Long offset = location.getKey();
            Long size = location.getValue();
            if (newEmptiness.shift + newEmptiness.size == location.getKey()) {
                PlaceOfValue mergedEmpties = new PlaceOfValue(numberOfFile,
                        newEmptiness.shift, newEmptiness.size + size);
                startsOfFreePlaces.get(numberOfFile).put(mergedEmpties.shift, mergedEmpties.size);
                endsOfFreePlaces.get(numberOfFile).put(
                        mergedEmpties.shift + mergedEmpties.size, mergedEmpties.size);
                startsOfFreePlaces.get(numberOfFile).remove(offset);
                endsOfFreePlaces.get(numberOfFile).remove(offset + size);
                newEmptiness = mergedEmpties;
                mergedNext = true;
                break;
            }
        }
        for (Map.Entry<Long, Long> location : endsOfFreePlaces.get(numberOfFile).entrySet()) {
            Long offset = location.getKey() - location.getValue();
            Long size = location.getValue();
            if (newEmptiness.shift == offset + size) {
                PlaceOfValue mergedEmpties = new PlaceOfValue(newEmptiness.numberOfFile,
                        offset, newEmptiness.size + size);
                endsOfFreePlaces.get(numberOfFile).put(
                        mergedEmpties.shift + mergedEmpties.size, mergedEmpties.size);
                startsOfFreePlaces.get(numberOfFile).put(
                        mergedEmpties.shift, mergedEmpties.size);
                startsOfFreePlaces.get(numberOfFile).remove(offset);
                endsOfFreePlaces.get(numberOfFile).remove(offset + size);
                mergedPrev = true;
                break;
            }
        }
        if (!mergedNext && !mergedPrev) {
            startsOfFreePlaces.get(numberOfFile).put(newEmptiness.shift, newEmptiness.size);
            endsOfFreePlaces.get(numberOfFile).put(newEmptiness.shift + newEmptiness.size, newEmptiness.size);
        }
    }

    private void readStorage() throws IOException {
        try (DataInputStream storage = new DataInputStream(new BufferedInputStream(
                new FileInputStream(storageFileName)))) {
            File emptyPlaceFile = new File(emptyPlacesFileName);
            if (!emptyPlaceFile.createNewFile()) {
                try (DataInputStream empties = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(emptyPlacesFileName)))) {
                    PlaceOfValue location = new PlaceOfValue(empties.readInt(),
                            empties.readLong(), empties.readLong());
                    startsOfFreePlaces.get(location.numberOfFile).put(location.shift, location.size);
                    endsOfFreePlaces.get(location.numberOfFile).put(location.shift + location.size, location.size);
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
                shiftTable.put(key, new PlaceOfValue(fileNumber, shift, size));
                setKeys.add(key);
            }
        }
    }

    private void writeLatestData() throws IOException {
        RandomAccessFile currentFile;
        Integer numberOfNewFile = listOfFiles.size();
        String newFileName = directoryName + File.separator + numberOfNewFile.toString();
        listOfFiles.put(numberOfNewFile, new RandomAccessFile(newFileName, "rw"));
        for (Map.Entry<K, V> entry : latestData.entrySet()) {
            if (entry.getValue() == null) {
                shiftTable.remove(entry.getKey());
                continue;
            }
            if (shiftTable.containsKey(entry.getKey())) {
                deallocate(shiftTable.get(entry.getKey()));
            }
            currentFile = listOfFiles.get(numberOfNewFile);
            currentFile.seek(currentFile.length());
            long start = currentFile.getFilePointer();
            valueSerializer.serialize(currentFile, entry.getValue());
            long finish = currentFile.getFilePointer();
            PlaceOfValue newLocation = new PlaceOfValue(numberOfNewFile, start, finish - start);
            shiftTable.put(entry.getKey(), newLocation);
            for (int numberOfFile = 0; numberOfFile < startsOfFreePlaces.size() - 1; numberOfFile++)
                for (Map.Entry<Long, Long> location : startsOfFreePlaces.get(numberOfFile).entrySet()) {
                    Long offset = location.getKey();
                    Long size = location.getValue();
                    if (size == finish - start) {
                        shiftTable.put(entry.getKey(), new PlaceOfValue(numberOfFile, offset, size));
                        currentFile = listOfFiles.get(numberOfFile);
                        currentFile.seek(offset);
                        valueSerializer.serialize(currentFile, entry.getValue());
                        startsOfFreePlaces.get(numberOfFile).remove(offset);
                        endsOfFreePlaces.get(numberOfFile).remove(offset + size);
                        deallocate(newLocation);
                        continue;
                    }
                    if (size > finish - start) {
                        shiftTable.put(entry.getKey(), new PlaceOfValue(numberOfNewFile, offset, size));
                        currentFile = listOfFiles.get(numberOfFile);
                        currentFile.seek(offset);
                        valueSerializer.serialize(currentFile, entry.getValue());
                        startsOfFreePlaces.get(numberOfFile).remove(offset);
                        endsOfFreePlaces.get(numberOfFile).remove(offset + size);
                        startsOfFreePlaces.get(numberOfFile).put(offset + finish - start, size - finish + start);
                        endsOfFreePlaces.get(numberOfFile).put(offset + size, size - finish + start);
                        deallocate(newLocation);
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
                    System.out.println(e.getMessage());
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
        Lock lock = globalLock.readLock();
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
        writeLock.lock();
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
        }
    }

    @Override
    public void delete(K key) {
        checkClosed();
        Lock writeLock = globalLock.writeLock();
        writeLock.lock();
        try {
            setKeys.remove(key);
            if (!latestData.containsKey(key)) {
                deallocate(shiftTable.get(key));
                shiftTable.remove(key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        Lock lock = globalLock.readLock();
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
        Lock lock = globalLock.readLock();
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
        if (!startsOfFreePlaces.isEmpty()) {
            try (DataOutputStream empties = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(emptyPlacesFileName)))) {
                for (int numberOfFile = 0; numberOfFile < startsOfFreePlaces.size(); ++numberOfFile) {
                    for (Map.Entry<Long, Long> location : startsOfFreePlaces.get(numberOfFile).entrySet()) {
                        empties.writeInt(numberOfFile);
                        empties.writeLong(location.getKey());
                        empties.writeLong(location.getValue());
                    }
                }
            }
            try (DataOutputStream storage = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(storageFileName)))) {
                storage.writeInt(listOfFiles.size());
                storage.writeInt(shiftTable.size());
                for (Map.Entry<K, PlaceOfValue> entry : shiftTable.entrySet()) {
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

}