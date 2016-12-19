package ru.mipt.java2016.homework.g595.romanenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.romanenko.task2.MapProducer;
import ru.mipt.java2016.homework.g595.romanenko.task2.SSTable;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.StringSerializer;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignature;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;


/**
 * Типизированное хранилище
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/
public class Storage<K, V> implements KeyValueStorage<K, V> {

    /**
     * Internal structure
     */
    private final RandomAccessFile storage;

    private final Map<K, Integer> indices = new HashMap<>();

    private final String currentDirectoryPath;
    private final SerializationStrategy<K> keySerializationStrategy;
    private final SerializationStrategy<V> valueSerializationStrategy;
    private final FileDigitalSignature fileDigitalSignature;

    /**
     * Cache variables
     */
    //maybe will be later

    /**
     * Updated values
     */
    private final List<Map.Entry<K, V>> updatedValues = new ArrayList<>();

    /**
     * Constants
     */
    private final int maxUpdatedObjectsInMemory = 10; //10 * 10Kb = 100 Kb
    private final String defaultStorageName = "storage.db";

    /**
     * Internal state
     */
    private boolean isClosed = false;
    private int epochNumber = 0;
    private int totalAmount = 0;
    private int storagePosition = 0;
    private int keyOverwriteAmount = 0;


    public Storage(String path,
                   SerializationStrategy<K> keySerializationStrategy,
                   SerializationStrategy<V> valueSerializationStrategy,
                   FileDigitalSignature fileDigitalSignature,
                   Comparator<K> keyComparator) {

        this.currentDirectoryPath = path;
        this.fileDigitalSignature = fileDigitalSignature;
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        String tableListDBPath = path + File.separator + "tableList.db";
        try {
            if (!readTableList(tableListDBPath)) {
                throw new RuntimeException();
            }
        } catch (IOException ignore) {
            throw new IllegalStateException("File tableList exist, but subtables are uncorrected");
        }

        try {
            storage = new RandomAccessFile(path + File.separator + defaultStorageName, "rw");
        } catch (FileNotFoundException e) {
            throw new MalformedDataException();
        }
    }

    private V loadValue(K key) {
        if (!indices.containsKey(key)) {
            return null;
        }
        V result;
        try {
            Integer offset = indices.get(key);
            if (offset != storagePosition) {
                storage.seek(offset);
            }
            InputStream stream = Channels.newInputStream(storage.getChannel());
            result = valueSerializationStrategy.deserializeFromStream(stream);
            storagePosition = offset + valueSerializationStrategy.getBytesSize(result);
        } catch (IOException e) {
            throw new MalformedDataException("Index exist, but value not");
        }
        return result;
    }

    private byte[] loadValueWithoutDeserialization(K key) {
        if (!indices.containsKey(key)) {
            return null;
        }
        byte[] result;
        try {
            Integer offset = indices.get(key);
            if (offset != storagePosition) {
                storage.seek(offset);
            }
            InputStream stream = Channels.newInputStream(storage.getChannel());
            result = valueSerializationStrategy.readValueAsBytes(stream);
            storagePosition = offset + result.length;
        } catch (IOException e) {
            throw new MalformedDataException("Index exist, but value not");
        }
        return result;
    }

    /**
     * Read tableList database and validate all
     *
     * @param tableListDBPath path to tableListDB
     * @return true if all is ok, false otherwise
     * @throws IOException if has system io error
     */
    private boolean readTableList(String tableListDBPath) throws IOException {
        SSTable<String, String> tablesList = new SSTable<>(tableListDBPath,
                StringSerializer.getInstance(), StringSerializer.getInstance(), fileDigitalSignature);
        fileDigitalSignature.signFileWithDefaultSignName(tableListDBPath);

        HashSet<String> keySet = new HashSet<>();
        Iterator<String> it = tablesList.readKeys();
        while (it.hasNext()) {
            keySet.add(it.next());
        }
        if (keySet.isEmpty()) {
            tablesList.close();
            return true;
        }

        totalAmount = Integer.parseInt(tablesList.getValue("DBSize"));
        keyOverwriteAmount = Integer.parseInt(tablesList.getValue("DBOverwriteAmount"));

        File file = new File(currentDirectoryPath + File.separator + defaultStorageName);
        if (!(file.exists() && !file.isDirectory())) {
            return false;
        }
        String tableNamePath = currentDirectoryPath + File.separator + defaultStorageName;
        boolean validationOk = fileDigitalSignature.validateFileSignWithDefaultSignName(tableNamePath);
        if (!validationOk) {
            throw new IllegalStateException("Invalid database");
        }

        String indicesTableName = currentDirectoryPath + File.separator + tablesList.getValue("Indices");
        SSTable<K, Integer> indicesTable = new SSTable<>(indicesTableName,
                keySerializationStrategy, IntegerSerializer.getInstance(), fileDigitalSignature);

        Iterator<K> keysIt = indicesTable.readKeys();

        while (keysIt.hasNext()) {
            K key = keysIt.next();
            indices.put(key, indicesTable.getValue(key));
        }

        return true;
    }


    /**
     * Read value from storage and cache it.
     * Doesn't contains more than maxCountObjectsInMemory objects.
     * Takes O(log n) time.
     *
     * @param key key to find
     * @return value for this key and null if key doesn't exist
     */
    @Override
    public V read(K key) {
        checkClosed();
        for (int i = updatedValues.size() - 1; i >= 0; i--) {
            if (updatedValues.get(i).getKey().equals(key)) {
                return updatedValues.get(i).getValue();
            }
        }
        return loadValue(key);
    }

    /**
     * Check existing of key in storage
     * Takes O(log n) time.
     *
     * @param key key to find
     * @return true for existing key, false otherwise
     */
    @Override
    public boolean exists(K key) {
        checkClosed();
        for (int i = updatedValues.size() - 1; i >= 0; i--) {
            if (updatedValues.get(i).getKey().equals(key)) {
                return true;
            }
        }
        return indices.containsKey(key);
    }

    /**
     * Write key, value pair to storage
     * Update if they exist
     * Flip to disk if updatedValues size more than maxCountObjectsInMemory
     *
     * @param key   key to write
     * @param value value to write
     */
    @Override
    public void write(K key, V value) {
        checkClosed();
        if (!exists(key)) {
            totalAmount++;
            epochNumber += 1;
        } else {
            ++keyOverwriteAmount;
        }

        updatedValues.add(new AbstractMap.SimpleEntry<>(key, value));
        if (updatedValues.size() >= maxUpdatedObjectsInMemory) {
            flipUpdatedValues();
        }
    }

    private void flipUpdatedValues() {
        try {
            if (storagePosition != storage.length()) {
                storage.seek(storage.length());
            }
            storagePosition = (int) storage.length();

            OutputStream fileOutputStream = Channels.newOutputStream(storage.getChannel());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            for (Map.Entry<K, V> entry : updatedValues) {
                indices.put(entry.getKey(), storagePosition);
                valueSerializationStrategy.serializeToStream(entry.getValue(), bufferedOutputStream);
                storagePosition += valueSerializationStrategy.getBytesSize(entry.getValue());
            }
            bufferedOutputStream.flush();
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        updatedValues.clear();
    }

    /**
     * Check is file closed
     */
    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("File is closed");
        }
    }

    /**
     * Delete key from storage
     *
     * @param key key to delete
     */
    @Override
    public void delete(K key) {
        checkClosed();
        epochNumber += 1;
        if (exists(key)) {
            totalAmount -= 1;
        }
        updatedValues.remove(key);
        indices.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        return new StorageIterator();
    }

    /**
     * Get size of storage
     *
     * @return size of storage
     */
    @Override
    public int size() {
        checkClosed();
        return totalAmount;
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        epochNumber += 1;
        isClosed = true;
        if (!updatedValues.isEmpty()) {
            flipUpdatedValues();
        }
        try {

            String pathPrefix = currentDirectoryPath + File.separator;

            if (keyOverwriteAmount >= totalAmount / 2) {
                rebuildStorage();
            }
            storage.close();
            SSTable<String, String> tablesDB = new SSTable<>(
                    pathPrefix + "tableList.db",
                    StringSerializer.getInstance(),
                    StringSerializer.getInstance(),
                    fileDigitalSignature);

            Map<String, String> tablesListMap = new HashMap<>();
            tablesListMap.put("DBSize", Integer.toString(totalAmount));
            tablesListMap.put("DBOverwriteAmount", Integer.toString(keyOverwriteAmount));
            tablesListMap.put("Indices", "indices.db");
            tablesDB.rewrite(new MapProducer<>(tablesListMap));
            tablesDB.close();

            SSTable<K, Integer> indicesTable = new SSTable<>(
                    pathPrefix + "indices.db",
                    keySerializationStrategy, IntegerSerializer.getInstance(), fileDigitalSignature);
            indicesTable.rewrite(new MapProducer<>(indices));
            indicesTable.close();

            fileDigitalSignature.signFileWithDefaultSignName(pathPrefix + defaultStorageName);
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
            exp.printStackTrace();
        }
    }

    private void rebuildStorage() throws IOException {
        String pathPrefix = currentDirectoryPath + File.separator;
        FileOutputStream fileOutputStream = new FileOutputStream(pathPrefix + "tempStorage.db");
        BufferedOutputStream newStorageStream = new BufferedOutputStream(fileOutputStream);
        Integer totalLength = 0;
        for (Map.Entry<K, Integer> entry : indices.entrySet()) {
            byte[] bytes = loadValueWithoutDeserialization(entry.getKey());
            newStorageStream.write(bytes);
            entry.setValue(totalLength);
            totalLength += bytes.length;
        }

        newStorageStream.flush();
        newStorageStream.close();
        storage.close();

        File storageFile = new File(pathPrefix + defaultStorageName);
        File newStorageFile = new File(pathPrefix + "tempStorage.db");
        Files.move(newStorageFile.toPath(), storageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        newStorageFile.delete();
    }

    public class StorageIterator implements Iterator<K> {

        private final Set<K> cachedKeys = new HashSet<>();
        private Iterator<K> updatedValuesIterator;
        private Iterator<K> indicesIterator;
        private final int currentEpochNumber;
        private K nextValue = null;

        private StorageIterator() {
            currentEpochNumber = epochNumber;
            updatedValuesIterator = updatedValues.stream().map(Map.Entry::getKey).iterator();
            indicesIterator = indices.keySet().iterator();
            getNext();
        }

        private void checkEpochNumber() {
            if (currentEpochNumber != epochNumber) {
                throw new ConcurrentModificationException();
            }
        }

        private void getNext() {
            checkEpochNumber();
            nextValue = null;
            while (updatedValuesIterator.hasNext()) {
                nextValue = updatedValuesIterator.next();
                if (cachedKeys.contains(nextValue)) {
                    continue;
                }
                cachedKeys.add(nextValue);
                return;
            }
            boolean findValue = false;
            while (indicesIterator.hasNext()) {
                nextValue = indicesIterator.next();
                if (!cachedKeys.contains(nextValue)) {
                    cachedKeys.add(nextValue);
                    findValue = true;
                    break;
                }
            }
            if (!findValue) {
                nextValue = null;
            }
        }

        @Override
        public boolean hasNext() {
            checkEpochNumber();
            return nextValue != null;
        }

        @Override
        public K next() {
            K result = nextValue;
            getNext();
            return result;
        }
    }
}
