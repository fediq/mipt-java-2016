package ru.mipt.java2016.homework.g595.ulyanin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.ulyanin.task2.Serializer;
import ru.mipt.java2016.homework.g595.ulyanin.task2.StringSerializer;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.nio.channels.FileLock;
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
            try {
                createLockFile();
                associatedFile.createNewFile();
                storage = createNewSSTable(constructDBName(createNewGeneration()));
            } catch (IOException error) {
                System.out.println("database already in use");
            }
        }
    }

    private void createLockFile() throws IOException {
        RandomAccessFile lockFile = new RandomAccessFile(storageFileName + ".lock", "rw");
        FileLock lock = lockFile.getChannel().lock();
    }

    private SSTable<K, V> createNewSSTable(String dbName)
            throws NoSuchAlgorithmException, IOException, ValidationException {
        return new SSTable<>(workingDirectory, dbName, keySerializer, valueSerializer);
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
        String dataBaseHash = StringSerializer.getInstance().deserialize(dataIS);
        String databaseName = StringSerializer.getInstance().deserialize(dataIS);
        storage = createNewSSTable(databaseName);
        String storageHash = storage.getDataBaseHash();
        if (!dataBaseHash.equals(storageHash)) {
            throw new IllegalArgumentException("It is not a file of dataBase");
        }
        dataIS.close();
    }

    @Override
    public V read(K key) {
        return storage.read(key);
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
    }

    @Override
    public void delete(K key) {
        storage.delete(key);
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
        if (state == StorageState.CLOSED) {
            return;
        }
        state = StorageState.CLOSED;
        FileOutputStream fileOutputStream = new FileOutputStream(associatedFile);
        DataOutputStream dataOS = new DataOutputStream(fileOutputStream);
        StringSerializer.getInstance().serialize(storage.getDataBaseHash(), dataOS);
        StringSerializer.getInstance().serialize(storage.getDBName(), dataOS);
        dataOS.close();
        fileOutputStream.close();
        storage.close();
    }
}
