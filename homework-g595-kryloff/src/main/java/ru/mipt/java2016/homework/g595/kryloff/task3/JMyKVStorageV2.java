package ru.mipt.java2016.homework.g595.kryloff.task3;

import java.io.File;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kryloff Gregory
 * @since 30.10.16
 */
public class JMyKVStorageV2<K, V> implements KeyValueStorage<K, V> {

    private static final Integer MAX_CACHE_SIZE = 500;
    private Map<K, V> cacheWrote;
    private Map<K, V> cacheRead;
    private Map<K, Long> offsets;
    private boolean isClosed;

    private JMySerializerInterface<K> keySerializer;
    private JMySerializerInterface<V> valueSerializer;
    private final JMyLongSerializer longSerializer;
    private final JMyIntegerSerializer integerSerializer;

    private String path;
    private final String storageName = "storage.db";
    private final String ptrsFileName = "ptrs.txt";

    private File storage;
    private RandomAccessFile rafStorage;

    private File ptrsFile;
    private RandomAccessFile rafPtrsFile;


    public JMyKVStorageV2(String pathArguement, JMySerializerInterface<K> keySerializerArguement,
            JMySerializerInterface<V> valueSerializerArguement) throws IOException {
        this.longSerializer = new JMyLongSerializer();
        this.integerSerializer = new JMyIntegerSerializer();
        isClosed = false;
        path = pathArguement;
        keySerializer = keySerializerArguement;
        valueSerializer = valueSerializerArguement;

        cacheWrote = new HashMap<>();
        cacheRead = new HashMap<>();
        offsets = new HashMap<>();
        try {
            storage = new File(pathArguement, storageName);
            rafStorage = new RandomAccessFile(storage, "rw");
            ptrsFile = new File(pathArguement, ptrsFileName);
        } catch (Exception ex) {
            System.out.println("Cannot create files or RAF");
        }

        if (ptrsFile.exists()) {
            getOffsets();
        }
    }

    private void getOffsets() throws IOException {

        Map<K, V> map = new HashMap<>();
        int count;
        int hash;
        RandomAccessFile raFile;
        try {
            raFile = new RandomAccessFile(ptrsFile, "rw");
            raFile.seek(0);
            count = raFile.readInt();
            hash = raFile.readInt();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read from RAF or create it");
        }
        K currentKey;
        Long currentOffset;
        for (int i = 0; i < count; ++i) {
            currentKey = keySerializer.deSerialize(raFile);
            currentOffset = longSerializer.deSerialize(raFile);
            offsets.put(currentKey, currentOffset);
        }
        if (hash != offsets.hashCode()) { //hashes are not equal
            throw new RuntimeException("File has been changed");
        }
    }

    private void saveData(Map<K, V> map) throws IOException {
        //System.out.println("Writing data");
        rafStorage.seek(rafStorage.length());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            offsets.put(entry.getKey(), rafStorage.getFilePointer());
            valueSerializer.serialize(rafStorage, entry.getValue());
        }
        //System.out.println("Data got");
    }

    private void saveOffsets() throws IOException {
        rafPtrsFile = new RandomAccessFile(ptrsFile, "rw");
        rafPtrsFile.seek(0);
        integerSerializer.serialize(rafPtrsFile, offsets.size());
        integerSerializer.serialize(rafPtrsFile, offsets.hashCode());
        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            keySerializer.serialize(rafPtrsFile, entry.getKey());
            longSerializer.serialize(rafPtrsFile, entry.getValue());
        }
    }

    private V find(K key, Long offset) throws IOException {

        rafStorage.seek(offset);
        return valueSerializer.deSerialize(rafStorage);
    }

    @Override
    public V read(K key) {
        //System.out.println("reading");
        checkNotClosed();
        if (!exists(key)) {
            return null;
        }

        if (cacheWrote.containsKey(key)) {
            return cacheWrote.get(key);
        }
        if (cacheRead.containsKey(key)) {
            return cacheRead.get(key);
        }

        try {
            return find(key, offsets.get(key));
        } catch (IOException ex) {
            Logger.getLogger(JMyKVStorageV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public boolean exists(K key) {
        return offsets.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        offsets.put(key, (long) -1);
        cacheWrote.put(key, value);

        if (cacheWrote.size() >= MAX_CACHE_SIZE) {
            try {
                saveData(cacheWrote);
            } catch (IOException ex) {
                Logger.getLogger(JMyKVStorageV2.class.getName()).log(Level.SEVERE, null, ex);
            }
            cacheWrote.clear();
        }
    }

    @Override
    public void delete(K key) {
        offsets.remove(key);
        cacheRead.remove(key);
        cacheWrote.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        checkNotClosed();
        return offsets.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        saveData(cacheWrote);
        saveOffsets();

        cacheRead = null;
        cacheWrote = null;
        offsets = null;
        rafPtrsFile.close();
        rafStorage.close();
        isClosed = true;
    }

    private void checkNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("Already closed");
        }
    }
}
