package ru.mipt.java2016.homework.g595.ulyanin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.ulyanin.task2.Serializer;
import ru.mipt.java2016.homework.g595.ulyanin.task2.StringSerializer;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ulyanin
 * @since 15.11.16.
 */
public class HighPerformancePreservingKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private enum StorageState { CLOSED, OPENED }

    private static final int MAX_MEMORY_BYTES = 40 * 1024 * 1024;

    private static final String DEFAULT_DB_NAME = "HPPKVStorage";

    private static final String STORAGE_VALIDATE_STRING = "MapPreservingStorage";

    private static int totalGenerationNumber = 0;

    private String storageFileName;
    private String workingDirectory;
    private StorageState state;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;

    private HashMap<K, V> memTable;
    private SSTable<K, V> storage;
    private File associatedFile;

    HighPerformancePreservingKeyValueStorage(String workingDirectory,
                                             Serializer<K> keySerializer,
                                             Serializer<V> valueSerializer)
            throws IOException, ValidationException, NoSuchAlgorithmException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.workingDirectory = workingDirectory;
        memTable = new HashMap<>();
        File tmp = new File(workingDirectory);
        storageFileName = workingDirectory;
        if (tmp.exists()) {
            if (tmp.isDirectory()) {
                storageFileName += File.separator + DEFAULT_DB_NAME;
            }
        } else {
            throw new IllegalArgumentException("file " + workingDirectory + " does not exist");
        }
        state = StorageState.OPENED;
        associatedFile = new File(storageFileName);
        if (associatedFile.exists()) {
            readFromFile(associatedFile);
        } else {
            associatedFile.createNewFile();
            storage = createNewSSTable(constructDBName(createNewGeneration()));
        }
    }

    private SSTable<K, V> createNewSSTable(String dbName)
            throws NoSuchAlgorithmException, IOException, ValidationException {
        return new SSTable<K, V>(workingDirectory, dbName, keySerializer, valueSerializer);
    }

    private int createNewGeneration() {
        return totalGenerationNumber++;
    }

    private String constructDBName(int generation) {
        return DEFAULT_DB_NAME + Integer.toString(generation);
    }

    private void readFromFile(File target) throws IOException, ValidationException, NoSuchAlgorithmException {
        FileInputStream fileInputStream = new FileInputStream(target);

        DataInputStream dataIS = new DataInputStream(fileInputStream);
        if (!StringSerializer.getInstance().deserialize(dataIS).equals(STORAGE_VALIDATE_STRING)) {
            throw new IllegalArgumentException("It is not a file of dataBase");
        }
        String databaseName = StringSerializer.getInstance().deserialize(dataIS);
        storage = createNewSSTable(databaseName);
        dataIS.close();
    }

    private V readFromCache(K key) {
        return null;
    }

    @Override
    public V read(K key) {
        V value = readFromCache(key);
        if (value == null) {
            value = storage.read(key);
        }
        return value;
    }

    @Override
    public boolean exists(K key) {
        return storage.exists(key);
    }

    @Override
    public void write(K key, V value) {
        try {
            storage.appendEntry(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (memTable.size() > 1000) {
//            writeMemTableToDisk();
//        }
    }

    private void writeMemTableToDisk() {
        try {
            storage.appendHashMapData(memTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        memTable.clear();
    }

    @Override
    public void delete(K key) {
        if (memTable.containsKey(key)) {
            memTable.remove(key);
        } else {
            storage.delete(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        return storage.readKeys();
    }

    @Override
    public int size() {
        return storage.size() + memTable.size();
    }

    @Override
    public void close() throws IOException {
        writeMemTableToDisk();
        FileOutputStream fileOutputStream = new FileOutputStream(associatedFile);
        DataOutputStream dataOS = new DataOutputStream(fileOutputStream);
        StringSerializer.getInstance().serialize(STORAGE_VALIDATE_STRING, dataOS);
        StringSerializer.getInstance().serialize(storage.getDBName(), dataOS);
        dataOS.close();
        storage.close();
    }
}
