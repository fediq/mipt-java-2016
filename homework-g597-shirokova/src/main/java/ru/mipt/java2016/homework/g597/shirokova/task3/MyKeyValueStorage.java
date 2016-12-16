package ru.mipt.java2016.homework.g597.shirokova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final int MAX_SIZE_OF_LATEST_DATA = 1300;

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;

    private Map<K, V> latestData = new HashMap<K, V>();
    private Set<K> keySet = new HashSet<K>();
    private ArrayList<Map<Long, Long>> startsOfFreePlaces = new ArrayList<>();
    private ArrayList<Map<Long, Long>> endsOfFreePlaces = new ArrayList<>();
    private Map<K, PlaceOfValue> offsetTable = new HashMap<K, PlaceOfValue>();
    private ArrayList<RandomAccessFile> listOfFiles = new ArrayList<>();

    private ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();

    private String storageFileName;
    private String directoryName;
    private String freeSpacesFileName;

    private boolean isClosed = false;
    private boolean existanceOfFreeSpace = false;

    MyKeyValueStorage(String currentPath, SerializationStrategy<K> serializerForKeys,
                      SerializationStrategy<V> serializerForValues) throws IOException {
        keySerializer = serializerForKeys;
        valueSerializer = serializerForValues;
        directoryName = currentPath;
        storageFileName = directoryName + File.separator + "Storage";
        freeSpacesFileName = directoryName + File.separator + "Empties";
        File storageFile = new File(storageFileName);
        if (!storageFile.createNewFile()) {
            readStorage();
        }
    }

    private void deallocate(PlaceOfValue newEmptiness) {
        boolean mergedNext = false;
        boolean mergedPrev = false;
        Integer numberOfFile = newEmptiness.numberOfFile;
        Map<Long, Long> currentMapOfStarts = startsOfFreePlaces.get(numberOfFile);
        Map<Long, Long> currentMapOfEnds = endsOfFreePlaces.get(numberOfFile);
        if (!currentMapOfStarts.isEmpty()) {
            if (currentMapOfEnds.containsKey(newEmptiness.offset)) {
                Long offset = newEmptiness.offset - currentMapOfEnds.get(newEmptiness.offset);
                Long size = currentMapOfEnds.get(newEmptiness.offset);
                PlaceOfValue mergedEmpties = new PlaceOfValue(numberOfFile,
                        newEmptiness.offset, newEmptiness.size + size);
                currentMapOfStarts.put(mergedEmpties.offset, mergedEmpties.size);
                currentMapOfEnds.put(mergedEmpties.offset + mergedEmpties.size, mergedEmpties.size);
                currentMapOfStarts.remove(offset);
                currentMapOfEnds.remove(offset + size);
                newEmptiness = mergedEmpties;
                mergedPrev = true;
            }
            if (currentMapOfStarts.containsKey(newEmptiness.offset + newEmptiness.size)) {
                Long offset = newEmptiness.offset + newEmptiness.size;
                Long size = currentMapOfStarts.get(newEmptiness.offset + newEmptiness.size);
                PlaceOfValue mergedEmpties = new PlaceOfValue(newEmptiness.numberOfFile,
                        offset, newEmptiness.size + size);
                currentMapOfEnds.put(mergedEmpties.offset + mergedEmpties.size, mergedEmpties.size);
                currentMapOfStarts.put(mergedEmpties.offset, mergedEmpties.size);
                currentMapOfStarts.remove(offset);
                currentMapOfEnds.remove(offset + size);
                mergedNext = true;
            }
        }
        if (!mergedNext && !mergedPrev) {
            currentMapOfStarts.put(newEmptiness.offset, newEmptiness.size);
            currentMapOfEnds.put(newEmptiness.offset + newEmptiness.size, newEmptiness.size);
        }
        existanceOfFreeSpace = true;
    }

    private void readStorage() throws IOException {
        try (DataInputStream storage = new DataInputStream(new BufferedInputStream(
                new FileInputStream(storageFileName)))) {
            int countOfFiles = storage.readInt();
            for (Integer i = 0; i < countOfFiles; ++i) {
                File currentFile = new File(directoryName + File.separator + i.toString());
                listOfFiles.add(new RandomAccessFile(currentFile, "rw"));
                endsOfFreePlaces.add(new HashMap<>());
                startsOfFreePlaces.add(new HashMap<>());
            }
            int countOfData = storage.readInt();
            for (int i = 0; i < countOfData; ++i) {
                K key = keySerializer.deserialize(storage);
                int fileNumber = storage.readInt();
                long offset = storage.readLong();
                long size = storage.readLong();
                offsetTable.put(key, new PlaceOfValue(fileNumber, offset, size));
                keySet.add(key);
            }
            File emptyPlaceFile = new File(freeSpacesFileName);
            if (!emptyPlaceFile.createNewFile()) {
                try (DataInputStream empties = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(freeSpacesFileName)))) {
                    PlaceOfValue location = new PlaceOfValue(empties.readInt(),
                            empties.readLong(), empties.readLong());
                    startsOfFreePlaces.get(location.numberOfFile).put(location.offset, location.size);
                    endsOfFreePlaces.get(location.numberOfFile).put(location.offset + location.size, location.size);
                    existanceOfFreeSpace = true;
                }
            }
        }
    }

    private void writeLatestData() throws IOException {
        RandomAccessFile currentFile;
        Integer numberOfNewFile = listOfFiles.size();
        String newFileName = directoryName + File.separator + numberOfNewFile.toString();
        listOfFiles.add(new RandomAccessFile(newFileName, "rw"));
        for (Map.Entry<K, V> entry : latestData.entrySet()) {
            if (offsetTable.containsKey(entry.getKey())) {
                deallocate(offsetTable.get(entry.getKey()));
            }
            currentFile = listOfFiles.get(numberOfNewFile);
            currentFile.seek(currentFile.length());
            long start = currentFile.getFilePointer();
            valueSerializer.serialize(currentFile, entry.getValue());
            long finish = currentFile.getFilePointer();
            PlaceOfValue newLocation = new PlaceOfValue(numberOfNewFile, start, finish - start);
            offsetTable.put(entry.getKey(), newLocation);
            for (int numberOfFile = 0; numberOfFile < startsOfFreePlaces.size() - 1; ++numberOfFile) {
                for (Map.Entry<Long, Long> location : startsOfFreePlaces.get(numberOfFile).entrySet()) {
                    Long offset = location.getKey();
                    Long size = location.getValue();
                    if (size == finish - start) {
                        offsetTable.put(entry.getKey(), new PlaceOfValue(numberOfFile, offset, size));
                        currentFile = listOfFiles.get(numberOfFile);
                        currentFile.seek(offset);
                        valueSerializer.serialize(currentFile, entry.getValue());
                        startsOfFreePlaces.get(numberOfFile).remove(offset);
                        endsOfFreePlaces.get(numberOfFile).remove(offset + size);
                        deallocate(newLocation);
                        continue;
                    }
                    if (size > finish - start) {
                        offsetTable.put(entry.getKey(), new PlaceOfValue(numberOfNewFile, offset, size));
                        currentFile = listOfFiles.get(numberOfFile);
                        currentFile.seek(offset);
                        valueSerializer.serialize(currentFile, entry.getValue());
                        startsOfFreePlaces.get(numberOfFile).remove(offset);
                        endsOfFreePlaces.get(numberOfFile).remove(offset + size);
                        startsOfFreePlaces.get(numberOfFile).put(offset + finish - start, size - finish + start);
                        endsOfFreePlaces.get(numberOfFile).put(offset + size, size - finish + start);
                        existanceOfFreeSpace = true;
                        deallocate(newLocation);
                    }
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
            if (offsetTable.keySet().contains(key)) {
                int fileNumber = offsetTable.get(key).numberOfFile;
                long shift = offsetTable.get(key).offset;
                RandomAccessFile currentFile = listOfFiles.get(fileNumber);
                try {
                    currentFile.seek(shift);
                    return valueSerializer.deserialize(currentFile);
                } catch (IOException e) {
                    throw new MalformedDataException("There aren't necessary data");
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
            return keySet.contains(key);
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
            keySet.add(key);
            if (latestData.size() >= MAX_SIZE_OF_LATEST_DATA) {
                writeLatestData();
            }
        } catch (IOException e) {
            throw new MalformedDataException("Can't write latest data");
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
            keySet.remove(key);
            if (!latestData.containsKey(key)) {
                deallocate(offsetTable.get(key));
                offsetTable.remove(key);
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
            return keySet.iterator();
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
            return keySet.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        writeLatestData();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            if (existanceOfFreeSpace) {
                try (DataOutputStream empties = new DataOutputStream(new BufferedOutputStream(
                        new FileOutputStream(freeSpacesFileName)))) {
                    for (int numberOfFile = 0; numberOfFile < startsOfFreePlaces.size(); ++numberOfFile) {
                        for (Map.Entry<Long, Long> location : startsOfFreePlaces.get(numberOfFile).entrySet()) {
                            empties.writeInt(numberOfFile);
                            empties.writeLong(location.getKey());
                            empties.writeLong(location.getValue());
                        }
                    }
                }
            } else {
                File freeSpaceFile = new File(freeSpacesFileName);
                freeSpaceFile.delete();
            }
            try (DataOutputStream storage = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(storageFileName)))) {
                storage.writeInt(listOfFiles.size());
                storage.writeInt(offsetTable.size());
                for (Map.Entry<K, PlaceOfValue> entry : offsetTable.entrySet()) {
                    keySerializer.serialize(storage, entry.getKey());
                    storage.writeInt(entry.getValue().numberOfFile);
                    storage.writeLong(entry.getValue().offset);
                    storage.writeLong(entry.getValue().size);
                }
            }
            latestData = null;
            isClosed = true;
            for (RandomAccessFile currentFile : listOfFiles) {
                currentFile.close();
            }
        } finally {
            lock.unlock();
        }
    }

    private static class PlaceOfValue {

        private int numberOfFile;
        private long offset;
        private long size;

        PlaceOfValue(int newFileNumber, long newOffset, long newSize) {
            numberOfFile = newFileNumber;
            offset = newOffset;
            size = newSize;
        }
    }

}