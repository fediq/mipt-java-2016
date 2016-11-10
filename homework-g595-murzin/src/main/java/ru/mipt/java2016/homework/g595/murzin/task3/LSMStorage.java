package ru.mipt.java2016.homework.g595.murzin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dima on 05.11.16.
 */
public class LSMStorage<K, V> implements KeyValueStorage<K, V> {

    public static final String KEYS_FILE_NAME = "keys.dat";
    public static final int MAX_CACHE_SIZE = 10;

    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private final Comparator<K> comparator;
    private FileLock lock;

    private File storageDirectory;

    private Map<K, Offset> keys = new HashMap<>();
    private Map<K, V> cache = new HashMap<>(MAX_CACHE_SIZE);
    private int numberTablesOnDisk;
    private ArrayList<BufferedRandomAccessFile> sstableFiles = new ArrayList<>();
    volatile private boolean isClosed;

    public LSMStorage(String path,
                      SerializationStrategy<K> keySerializationStrategy,
                      SerializationStrategy<V> valueSerializationStrategy,
                      Comparator<K> comparator) {
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        this.comparator = comparator;

        storageDirectory = new File(path);
        if (!storageDirectory.isDirectory() || !storageDirectory.exists()) {
            throw new RuntimeException("Path " + path + " is not a valid directory name");
        }

        synchronized (LSMStorage.class) {
            try {
                // It is between processes lock!!!
                lock = new RandomAccessFile(new File(path, ".lock"), "rw").getChannel().lock();
            } catch (IOException e) {
                throw new RuntimeException("Can't create lock file", e);
            }
        }

        readAllKeys();
        if (getTableFile(0).exists()) {
            numberTablesOnDisk = 1;
            try {
                sstableFiles.add(new BufferedRandomAccessFile(getTableFile(0)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Кажется вы умудрились удалить файл базы сразу после того, как мы проверили, что он существует...", e);
            }
        } else {
            numberTablesOnDisk = 0;
        }
    }

    private String getTableFileName(int index) {
        return "table" + index + ".dat";
    }

    private File getTableFile(int index) {
        return new File(storageDirectory, getTableFileName(index));
    }

    private void readAllKeys() {
        File keysFile = new File(storageDirectory, KEYS_FILE_NAME);
        if (!keysFile.exists()) {
            return;
        }
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(keysFile)))) {
            int n = input.readInt();
            for (int i = 0; i < n; i++) {
                K key = keySerializationStrategy.deserializeFromStream(input);
                Offset offset = Offset.STRATEGY.deserializeFromStream(input);
                keys.put(key, offset);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read from keys file " + keysFile.getAbsolutePath(), e);
        }
    }

    private void writeAllKeys() throws IOException {
        File keysFile = new File(storageDirectory, KEYS_FILE_NAME);
        if (keys.isEmpty()) {
            Files.delete(keysFile.toPath());
            return;
        }
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(keysFile)))) {
            output.writeInt(keys.size());
            for (Map.Entry<K, Offset> entry : keys.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                Offset.STRATEGY.serializeToStream(entry.getValue(), output);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write to keys file " + keysFile.getAbsolutePath(), e);
        }
    }

    private void checkForCacheSize() {
        if (cache.size() <= MAX_CACHE_SIZE) {
            return;
        }
        pushCacheToDisk();
        if (sstableFiles.size() == 10) {

        }
    }

    private void pushCacheToDisk() {
        if (cache.isEmpty()) {
            return;
        }
        int newTableIndex = numberTablesOnDisk++;
        File newTableFile = getTableFile(newTableIndex);
        try {
            Files.createFile(newTableFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Can't create one of table files " + newTableFile.getAbsolutePath(), e);
        }

        try (FileOutputStream fileOutput = new FileOutputStream(newTableFile);
             FileChannel fileChannel = fileOutput.getChannel();
             DataOutputStream output = new DataOutputStream(fileOutput)) {
            output.writeInt(cache.size());
            for (Map.Entry<K, V> entry : cache.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                keys.put(entry.getKey(), new Offset(newTableIndex, fileChannel.position()));
                valueSerializationStrategy.serializeToStream(entry.getValue(), output);
            }
            sstableFiles.add(new BufferedRandomAccessFile(getTableFile(newTableIndex)));
        } catch (IOException e) {
            throw new RuntimeException("Can't write to one of table files " + newTableFile.getAbsolutePath(), e);
        }
        cache = new HashMap<>();
    }

    private KeyValueInputStream<K, V> getTableInputStream(File tableFile) throws IOException {
        return new KeyValueInputStream<>(tableFile, keySerializationStrategy, valueSerializationStrategy);
    }

    private KeyValueOutputStream<K, V> getTableOutputStream(File tableFile, int numberEntries) throws IOException {
        return new KeyValueOutputStream<>(tableFile, keySerializationStrategy, valueSerializationStrategy, numberEntries);
    }

    private void mergeAllTableFiles() throws IOException {
        if (keys.isEmpty()) {
            for (int i = 0; i < numberTablesOnDisk; i++) {
                Files.delete(getTableFile(i).toPath());
            }
            sstableFiles.clear();
            return;
        }

        pushCacheToDisk();
        File lastTable = getTableFile(numberTablesOnDisk - 1);
        for (int i = numberTablesOnDisk - 2; i >= 0; i--) {
            mergeTwoTableFiles(getTableFile(i), lastTable, lastTable = File.createTempFile("table", ".dat"));
        }

        if (numberTablesOnDisk == 1) {
            Files.move(lastTable.toPath(), (lastTable = File.createTempFile("table", ".dat")).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            for (int i = 0; i < numberTablesOnDisk; i++) {
                Files.delete(getTableFile(i).toPath());
            }
        }

        // Удалим повторяющиеся записи +записи, ключи которых не содержатся в keys
        for (Map.Entry<K, Offset> entry : keys.entrySet()) {
            entry.setValue(Offset.NONE);
        }
        int actualNumberEntries = 0;
        try (KeyValueInputStream<K, V> input = getTableInputStream(lastTable);
             KeyValueOutputStream<K, V> output = getTableOutputStream(getTableFile(0), input.numberEntries)) {
            while (input.hasNext()) {
                Map.Entry<K, V> entry = input.readEntry();
                K key = entry.getKey();
                if (!keys.containsKey(key)) {
                    continue;
                }
                Offset offset = keys.get(key);
                if (offset != Offset.NONE) {
                    // Значит такой ключ мы уже обработали
                    continue;
                }
                ++actualNumberEntries;
                keys.put(key, new Offset(0, output.writeEntry(entry)));
            }
        }
        // change number entries
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(getTableFile(0), "rw")) {
            randomAccessFile.writeInt(actualNumberEntries);
        }

        sstableFiles.clear();
        sstableFiles.add(new BufferedRandomAccessFile(getTableFile(0)));
    }

    private void mergeTwoTableFiles(File file1, File file2, File result) throws IOException {
        try (KeyValueInputStream<K, V> input1 = getTableInputStream(file1);
             KeyValueInputStream<K, V> input2 = getTableInputStream(file2);
             KeyValueOutputStream<K, V> output = getTableOutputStream(result, input1.numberEntries + input2.numberEntries)) {
            while (input1.hasNext() || input2.hasNext()) {
                if (!input1.hasNext()) {
                    output.writeEntry(input2.readEntry());
                } else if (!input2.hasNext()) {
                    output.writeEntry(input1.readEntry());
                } else {
                    Map.Entry<K, V> entry1 = input1.peekEntry();
                    Map.Entry<K, V> entry2 = input2.peekEntry();
                    output.writeEntry(comparator.compare(entry1.getKey(), entry2.getKey()) < 0 ? input1.readEntry() : input2.readEntry());
                }
            }
        }
    }

    private void checkForClosed() {
        if (isClosed) {
            throw new RuntimeException("Access to closed storage");
        }
    }

    @Override
    public synchronized V read(K key) {
        checkForClosed();
        if (!keys.containsKey(key)) {
            return null;
        }
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Offset offset = keys.get(key);
        assert offset != null && offset.fileIndex != -1;
        BufferedRandomAccessFile bufferedRandomAccessFile = sstableFiles.get(offset.fileIndex);
        V value;
        try {
            bufferedRandomAccessFile.seek(offset.fileOffset);
            value = valueSerializationStrategy.deserializeFromStream(bufferedRandomAccessFile.dataInputStream);
            cache.put(key, value);
        } catch (IOException e) {
            throw new RuntimeException("Can't read from one of table files", e);
        }
        checkForCacheSize();
        return value;
    }

    @Override
    public synchronized boolean exists(K key) {
        checkForClosed();
        return keys.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        checkForClosed();
        keys.put(key, Offset.NONE);
        cache.put(key, value);
        checkForCacheSize();
    }

    @Override
    public synchronized void delete(K key) {
        checkForClosed();
        keys.remove(key);
        cache.remove(key);
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        checkForClosed();
        return keys.keySet().iterator();
    }

    @Override
    public synchronized int size() {
        checkForClosed();
        return keys.size();
    }

    @Override
    public synchronized void close() throws IOException {
        checkForClosed();
        mergeAllTableFiles();
        writeAllKeys();
        lock.release();
        isClosed = true;
    }
}
