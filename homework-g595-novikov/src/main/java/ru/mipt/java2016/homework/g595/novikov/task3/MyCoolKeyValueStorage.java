package ru.mipt.java2016.homework.g595.novikov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.novikov.task2.IntSerialization;
import ru.mipt.java2016.homework.g595.novikov.task2.MySerialization;
import org.apache.commons.collections.iterators.IteratorChain;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.*;

/**
 * Created by igor on 10/31/16.
 */
public class MyCoolKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String LOCK_FILENAME = "lock";
    private static final String INDICES_FILENAME = "indices";
    private static final int MAX_ADDED_BYTES = 1024 * 1024 * 10; // 10MB, what about changing name of this variable?
    private boolean closed = false;

    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    private MySerialization<Integer> intSerialization = new IntSerialization();

    private ArrayList<DBFile<K, V>> dbFiles = new ArrayList<>();
    private Map<K, Integer> fileIndex = new HashMap<K, Integer>();
    /*
    private LoadingCache<K, V> cache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<K, V>() {
        @Override
        public V load(K key) throws Exception {
            return dbFiles.get(fileIndex.get(key)).read(key);
        }
    }); */

    int getNewIndex() {
        int maxIndex = -1;
        for (DBFile dbFile : dbFiles) {
            maxIndex = Math.max(maxIndex, dbFile.getIndex());
        }
        return maxIndex + 1; /* this is a beta version of this function,
                                i believe it will be reimplemented sometimes */
    }

    private class Added implements Flushable { // wrapper over map of added keys-values
        private Map<K, V> added = new HashMap<>();
        private int addedSize;

        void checkFlush() throws IOException {
            if (addedSize >= MAX_ADDED_BYTES) {
                flush();
            }
        }

        void put(K key, V value) {
            if (added.containsKey(key)) {
                addedSize -= valueSerialization.getSizeSerialized(added.get(key));
            }
            addedSize += valueSerialization.getSizeSerialized(value);
            added.put(key, value);
            try {
                checkFlush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot flush added table");
            }
        }

        boolean containsKey(K key) {
            return added.containsKey(key);
        }

        V get(K key) {
            return added.get(key);
        }

        void remove(K key) {
            addedSize -= valueSerialization.getSizeSerialized(added.get(key));
            added.remove(key);
        }

        int size() {
            return added.size();
        }

        @Override
        public void flush() throws IOException {
            if (added.size() == 0) {
                return;
            }

            DBFile<K, V> dbFile = new DBFile<>(Collections.unmodifiableMap(added),
                    directoryName,
                    getNewIndex(),
                    keySerialization,
                    valueSerialization);
            dbFile.updateIndex(dbFiles.size(), fileIndex);
            dbFiles.add(dbFile);

            added.clear();
            addedSize = 0;
        }

        public Iterator<K> getIterator() {
            return Collections.unmodifiableMap(added).keySet().iterator();
        }
    }

    private Added added = new Added();

    private String directoryName;
    private File indices;
    private File lockFile;
    private FileLock lock;

    MyCoolKeyValueStorage(String myDirectoryName, MySerialization<K> myKeySerialization,
                          MySerialization<V> myValueSerialization) {
        keySerialization = myKeySerialization;
        valueSerialization = myValueSerialization;
        directoryName = myDirectoryName;

        try {
            File directory = new File(directoryName);
            if (!directory.exists() || !directory.isDirectory()) {
                throw new FileNotFoundException("directory not found");
            }
            readDatabase(directory);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MalformedDataException("Error during opening database");
        }
        System.out.println("Constructor call, size = " + size());
    }

    private void readDatabase(File directory) throws IOException {
        lockFile = new File(directory, LOCK_FILENAME);
        if (!lockFile.createNewFile()) {
            throw new IllegalStateException("Lock file already exists");
        }
        lock = new FileOutputStream(lockFile).getChannel().lock();

        indices = new File(directory, INDICES_FILENAME);
        if (indices.createNewFile()) {
            return;
        }
        DataInputStream indicesInput = new DataInputStream(new FileInputStream(indices));
        int cntSSTables = intSerialization.deserialize(indicesInput);
        for (int q = 0; q < cntSSTables; ++q) {
            int index = intSerialization.deserialize(indicesInput);
            dbFiles.add(new DBFile<K, V>(directory.getPath(), index, keySerialization, valueSerialization));
            dbFiles.get(q).updateIndex(q, fileIndex);
        }
    }

    @Override
    public V read(K key) {
        checkClosed();
        if (added.containsKey(key)) {
            return added.get(key);
        }

        if (fileIndex.get(key) == null) {
            return null;
        }
        V result = null;
        try {
            result = dbFiles.get(fileIndex.get(key)).read(key);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error during read key from dbFile");
        }

        return result;
    }

    @Override
    public boolean exists(K key) {
        checkClosed();
        return added.containsKey(key) || fileIndex.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkClosed();
        if (fileIndex.containsKey(key)) {
            dbFiles.get(fileIndex.get(key)).remove(key);
            fileIndex.remove(key);
        }
        added.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkClosed();
        if (added.containsKey(key)) {
            added.remove(key);
        } else {
            dbFiles.get(fileIndex.get(key)).remove(key);
            fileIndex.remove(key);
        }
    }

    private class MyIterator implements Iterator<K> { // non-static classes are cool!!!
        private IteratorChain iter = new IteratorChain();
        private Iterator<K> iterAdded = added.getIterator();
        private Iterator<K> iterFlushed = Collections.unmodifiableMap(fileIndex).keySet().iterator();

        MyIterator() {
            iter.addIterator(iterAdded);
            iter.addIterator(iterFlushed);
        }

        @Override
        public boolean hasNext() {
            checkClosed();
            return iter.hasNext();
        }

        @Override
        public K next() {
            checkClosed();
            return (K) iter.next();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        return new MyIterator();
    }

    @Override
    public int size() {
        checkClosed();
        return added.size() + fileIndex.size();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Close call, size = " + size());
        flush();
        lock.release();
        lockFile.delete();
        closed = true;
    }

    @Override
    public void flush() { // WHY NOT THROWS IOException ??? WHY NOT IMPLEMENTS Flushable
        checkClosed();
        String indicesFilename = String.format("%s%s%s", directoryName, File.separator, INDICES_FILENAME);
        try (DataOutputStream indicesStream = new DataOutputStream(
                new FileOutputStream(new File(indicesFilename)))) {
            added.flush();

            intSerialization.serialize(indicesStream, dbFiles.size());
            for (DBFile dbFile : dbFiles) {
                intSerialization.serialize(indicesStream, dbFile.getIndex());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("IOException during flush()");
        }

        try {
            for (DBFile dbFile : dbFiles) {
                dbFile.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("IOException durng flush()");
        }
    }

    void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Cannot access to closed database");
        }
    }
}
