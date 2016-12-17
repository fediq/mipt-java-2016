package ru.mipt.java2016.homework.g594.islamov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

class UpgradedKVStorage<K, V> implements KeyValueStorage<K, V> {
    private final ReadWriteLock readWriteLock;
    private final Lock writeLock;
    private final Lock readLock;
    private String fileName;
    private String hashOfFile;
    private HashMap<K, Position> positionOfKey;
    private Cache<K, V> cache;
    private HashMap<K, V> recentKV;
    private HashSet<K> usedKeys;
    private ArrayList<RandomAccessFile> files;
    private Adler32 integrity;

    private KVSSerializationInterface<K> keySerialization;
    private KVSSerializationInterface<V> valueSerialization;
    private boolean closed;

    private static final int MC_SIZEOFRECENTKV = 1024;
    private static final int MC_INITIALCAPACITY = 16;
    private static final int MC_NUMBER = 100;
    private static final int MC_SIZEOFCASH = 0;
    private static final int MC_MAINFILE = -1;
    private static final int MC_FIRSTFILE = 0;

    // Class to work with values of the required key
    private class Position {
        private int numberOfFile;
        private long offsetValue;

        Position(int numberOfFile, long offset) {
            this.numberOfFile = numberOfFile;
            this.offsetValue = offset;
        }
    }

    // Cache, where we find our key first
    private class Cache<K, V> extends LinkedHashMap<K, V> {
        Cache() {
            super(MC_INITIALCAPACITY, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > MC_SIZEOFCASH;
        }
    }

    UpgradedKVStorage(String path, KVSSerializationInterface serializedKey,
                      KVSSerializationInterface serializedValue) {
        readWriteLock = new ReentrantReadWriteLock();
        writeLock = readWriteLock.writeLock();
        readLock = readWriteLock.readLock();
        keySerialization = serializedKey;
        valueSerialization = serializedValue;
        fileName = path + "/storage";
        hashOfFile = path + "/hash.db";
        positionOfKey = new HashMap<K, Position>();
        cache = new Cache<>();
        recentKV = new HashMap<>();
        usedKeys = new HashSet<>();
        files = new ArrayList<>();
        closed = false;

        // Main file, where amount of files with data, keys' positions and hash are situated
        String mainFile = receiveFileName(MC_MAINFILE);
        File file = new File(mainFile);
        // File, that contains hash
        File integrityFile = new File(hashOfFile);

        if (!file.exists()) {
            try {
                file.createNewFile();
                if (!integrityFile.exists()) {
                    integrityFile.createNewFile();
                }
            } catch (IOException e) {
                throw new MalformedDataException("Failed at creating the file");
            }
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(mainFile));
                 DataOutputStream outHash = new DataOutputStream(new FileOutputStream(hashOfFile))) {
                writeLock.lock();
                // Amount of files
                out.writeInt(0);
                // Amount of positions of keys
                out.writeInt(0);
                // Initial hash
                outHash.writeInt(0);
                writeLock.unlock();
            } catch (IOException e) {
                throw new MalformedDataException("Failed at writing to the file");
            }
        }
        if (!integrityFile.exists()) {
            throw new MalformedDataException("File not found");
        }
        try (DataInputStream in = new DataInputStream(new FileInputStream(mainFile))) {
            readLock.lock();
            // Number of files with data
            int numberOfFiles = in.readInt();
            // Check the integrity of all our files
            checkIntegrity(numberOfFiles);
            // Remember all our files with data
            for (int i = MC_FIRSTFILE; i < MC_FIRSTFILE + numberOfFiles; ++i) {
                File currentFile = new File(receiveFileName(i));
                if (!currentFile.exists()) {
                    throw new MalformedDataException("File not found");
                }
                files.add(new RandomAccessFile(currentFile, "rw"));
            }
            // Remember all our used keys
            int numberOfKeys = in.readInt();
            for (int i = 0; i < numberOfKeys; ++i) {
                // Deserialized key
                K key = keySerialization.deserialize(in.readUTF());
                // File, that contains current key
                int numberOfFile = in.readInt();
                // The offset in this files
                long offset = in.readLong();
                positionOfKey.put(key, new Position(numberOfFile, offset));
                usedKeys.add(key);
            }
            readLock.unlock();
        } catch (IOException e) {
            throw new MalformedDataException("Failed at reading from file");
        }
    }

    // Check hash of all files
    private void checkIntegrity(int numberOfFiles) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(hashOfFile))) {
            readLock.lock();
            if (numberOfFiles != in.readInt()) {
                throw new MalformedDataException("Not a valid storage");
            }
            // Check the integrity of all files
            integrity = new Adler32();
            for (int i = MC_FIRSTFILE; i < MC_FIRSTFILE + numberOfFiles; ++i) {
                getHashOfFile(receiveFileName(i), integrity);
            }
            if (numberOfFiles != 0 && integrity.getValue() != in.readLong()) {
                throw new MalformedDataException("Not a valid storage");
            }
            readLock.unlock();
        } catch (IOException e) {
            throw new MalformedDataException("Failed at working with files");
        }
    }

    // Updates checksum for current file
    private void getHashOfFile(String currentFileName, Adler32 checkSum) {
        try (InputStream inStream = new BufferedInputStream(new FileInputStream(new File(currentFileName)));
             CheckedInputStream checkedInStream = new CheckedInputStream(inStream, checkSum)) {
            readLock.lock();
            byte[] buffer = new byte[MC_SIZEOFRECENTKV * MC_NUMBER];
            while (checkedInStream.read(buffer) != -1) {
                continue;
            }
            readLock.unlock();
        } catch (IOException e) {
            throw new MalformedDataException("File not found");
        }
    }

    // Receives name of file for the counter i
    private String receiveFileName(Integer i) {
        if (i.equals(MC_MAINFILE)) {
            return fileName + ".db";
        } else {
            return fileName + i.toString() + ".db";
        }
    }

    // Flushes all keys in recentKV to the new file
    private void flushRecentKV(boolean needToFlush) {
        if (needToFlush || recentKV.size() >= MC_SIZEOFRECENTKV) {
            int numberOfNewFile = files.size();
            String newFile = receiveFileName(numberOfNewFile);
            File file = new File(newFile);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new MalformedDataException("Failed at creating the file", e);
                }
            }
            try {
                files.add(new RandomAccessFile(newFile, "rw"));
                RandomAccessFile currentFile = files.get(numberOfNewFile);
                currentFile.setLength(0);
                currentFile.seek(0);
                writeLock.lock();
                for (Map.Entry<K, V> entry : recentKV.entrySet()) {
                    if (entry.getValue().equals(null)) {
                        positionOfKey.remove(entry.getKey());
                        continue;
                    }
                    Position newPosition = new Position(numberOfNewFile, currentFile.getFilePointer());
                    positionOfKey.put(entry.getKey(), newPosition);
                    currentFile.writeUTF(valueSerialization.serialize(entry.getValue()));
                }
                writeLock.lock();
                recentKV.clear();
                getHashOfFile(newFile, integrity);
            } catch (IOException e) {
                throw new MalformedDataException("Couldn't get RandomAccessFile");
            }
        }
    }

    @Override
    public V read(K key) {
        isStorageClosed();
        if (cache.keySet().contains(key)) {
            return cache.get(key);
        }
        if (recentKV.keySet().contains(key)) {
            return recentKV.get(key);
        }
        if (!positionOfKey.keySet().contains(key)) {
            return null;
        }
        int numberOfFile = positionOfKey.get(key).numberOfFile;
        long offsetValue = positionOfKey.get(key).offsetValue;
        RandomAccessFile currentFile = files.get(numberOfFile);
        try {
            currentFile.seek(offsetValue);
            V value = valueSerialization.deserialize(currentFile.readUTF());
            cache.put(key, value);
            return value;
        } catch (IOException e) {
            throw new MalformedDataException("Not found needed data");
        }
    }

    @Override
    public boolean exists(K key) {
        isStorageClosed();
        return (usedKeys.contains(key));
    }

    @Override
    public void write(K key, V value) {
        isStorageClosed();
        recentKV.put(key, value);
        usedKeys.add(key);
        flushRecentKV(false);
    }

    @Override
    public void delete(K key) {
        isStorageClosed();
        if (exists(key)) {
            positionOfKey.remove(key);
            cache.remove(key);
            usedKeys.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isStorageClosed();
        return usedKeys.iterator();
    }

    @Override
    public int size() {
        isStorageClosed();
        return usedKeys.size();
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        flushRecentKV(true);
        closed = true;
        String mainFile = receiveFileName(MC_MAINFILE);
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(mainFile))) {
            writeLock.lock();
            out.writeInt(files.size());
            out.writeInt(positionOfKey.size());
            for (Map.Entry<K, Position> entry : positionOfKey.entrySet()) {
                out.writeUTF(keySerialization.serialize(entry.getKey()));
                out.writeInt(entry.getValue().numberOfFile);
                out.writeLong(entry.getValue().offsetValue);
            }
            writeLock.unlock();
        }
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(hashOfFile))) {
            writeLock.lock();
            out.writeInt(files.size());
            for (int i = MC_FIRSTFILE; i < MC_FIRSTFILE + files.size(); ++i) {
                files.get(i).close();
            }
            out.writeLong(integrity.getValue());
            writeLock.unlock();
        }
        cache.clear();
        recentKV.clear();
        positionOfKey.clear();
        usedKeys.clear();
        files.clear();
    }

    private void isStorageClosed() throws MalformedDataException {
        if (closed) {
            throw new MalformedDataException("File has already been closed");
        }
    }
}