package ru.mipt.java2016.homework.g595.murzin.task3;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
    public static final int MAX_NEW_ENTRIES_SIZE = 10;

    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private final Comparator<K> comparator;
    private FileLock lock;
    private File storageDirectory;

    private Map<K, Offset> keys = new HashMap<>();
    private ArrayList<KeyInfo<K>[]> sstablesKeys = new ArrayList<>();
    private Map<K, V> newEntries = new HashMap<>(MAX_NEW_ENTRIES_SIZE);
    private Cache<K, V> cache = CacheBuilder
            .newBuilder()
            .maximumSize(32 * 1024 * 1024 / (50 * 1024))
            .build();
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
            sstablesKeys.add(keys
                    .entrySet()
                    .stream()
                    .map(entry -> new KeyInfo<>(entry.getKey(), entry.getValue().offsetInFile))
                    .sorted((keyInfo1, keyInfo2) -> comparator.compare(keyInfo1.key, keyInfo2.key))
                    .toArray(KeyInfo[]::new));
            try {
                sstableFiles.add(new BufferedRandomAccessFile(getTableFile(0)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Кажется вы умудрились удалить файл базы сразу после того, как мы проверили, что он существует...", e);
            }
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

    private void checkForNewEntriesSize() throws IOException {
        if (newEntries.size() <= MAX_NEW_ENTRIES_SIZE) {
            return;
        }
        pushNewEntriesToDisk();
        checkForMergeTablesOnDisk();
    }

    private void pushNewEntriesToDisk() {
        if (newEntries.isEmpty()) {
            return;
        }
        int newTableIndex = sstableFiles.size();
        File newTableFile = getTableFile(newTableIndex);
        try {
            Files.createFile(newTableFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Can't create one of table files " + newTableFile.getAbsolutePath(), e);
        }

        Map.Entry<K, V>[] newEntriesSorted = newEntries
                .entrySet()
                .stream()
                .sorted((entry1, entry2) -> comparator.compare(entry1.getKey(), entry2.getKey()))
                .toArray(Map.Entry[]::new);
        ArrayList<KeyInfo<K>> keyInfos = new ArrayList<>(newEntriesSorted.length);

        try (FileOutputStream fileOutput = new FileOutputStream(newTableFile);
             FileChannel fileChannel = fileOutput.getChannel();
             DataOutputStream output = new DataOutputStream(fileOutput)) {
            output.writeInt(newEntries.size());
            for (Map.Entry<K, V> entry : newEntriesSorted) {
                K key = entry.getKey();
                long position = fileChannel.position();
                keys.put(key, new Offset(newTableIndex, position));
                keyInfos.add(new KeyInfo<>(key, position));
                valueSerializationStrategy.serializeToStream(entry.getValue(), output);
            }
            sstableFiles.add(new BufferedRandomAccessFile(getTableFile(newTableIndex)));
        } catch (IOException e) {
            throw new RuntimeException("Can't write to one of table files " + newTableFile.getAbsolutePath(), e);
        }
            sstablesKeys.add(keyInfos.toArray(new KeyInfo[keyInfos.size()]));
        newEntries = new HashMap<>();
    }

    private void checkForMergeTablesOnDisk() throws IOException {
        // https://habrahabr.ru/post/251751/
        while (sstableFiles.size() > 1) {
            int n = sstableFiles.size() - 2;
            int[] tablesLength = sstablesKeys.stream().mapToInt(table -> table.length).toArray();
            if (n > 0 && tablesLength[n - 1] <= tablesLength[n] + tablesLength[n + 1]
                    || n - 1 > 0 && tablesLength[n - 2] <= tablesLength[n] + tablesLength[n - 1]) {
                if (tablesLength[n - 1] < tablesLength[n + 1]) {
                    n--;
                }
            } else if (n < 0 || tablesLength[n] > tablesLength[n + 1]) {
                break; // инвариант установлен
            }
            mergeTwoTableFiles(n);
        }
    }

    private class MergeInfo {
        public final int length;
        private final KeyInfo<K>[] keyInfos;
        private final FileInputStream input;
        private final long fileLength;

        public MergeInfo(int i) throws IOException {
            keyInfos = sstablesKeys.get(i);
            length = keyInfos.length;
            input = new FileInputStream(getTableFile(i));
            fileLength = sstableFiles.get(i).fileLength();
        }

        public K keyAt(int i) {
            return keyInfos[i].key;
        }

        private int getValueSize(int i) {
            long end = i == length - 1 ? fileLength : keyInfos[i + 1].offsetInFile;
            long start = keyInfos[i].offsetInFile;
            return (int) (end - start);
        }

        public int getMaxValueSize() {
            int maxSize = 0;
            for (int i = 0; i < length; i++) {
                maxSize = Math.max(maxSize, getValueSize(i));
            }
            return maxSize;
        }
    }

    // Сливает таблицы i и i + 1
    // Полученной таблице присваивается номер i
    // Кроме того индексы всех таблиц с номерами j > i + 1 уменьшаются на один
    private void mergeTwoTableFiles(int i) throws IOException {
        MergeInfo info1 = new MergeInfo(i);
        MergeInfo info2 = new MergeInfo(i + 1);

        int maxValueSize = Math.max(info1.getMaxValueSize(), info2.getMaxValueSize());
        byte[] buffer = new byte[maxValueSize];

        ArrayList<K> keysMerged = new ArrayList<>(info1.length + info2.length);
        File tempFile = Files.createTempFile("temp_table", "dat").toFile();
        try (FileOutputStream output = new FileOutputStream(tempFile)) {
            int i1 = 0;
            int i2 = 0;
            long position = 0;
            while (i1 < info1.length || i2 < info2.length) {
                K key1 = i1 == info1.length - 1 ? null : info1.keyAt(i1);
                K key2 = i2 == info2.length - 1 ? null : info2.keyAt(i2);
                int compare = key1 == null ? 1 : key2 == null ? -1 : comparator.compare(key1, key2);
                K key = compare == -1 ? key1 : key2;
                MergeInfo info = compare == -1 ? info1 : info2;

                keys.put(key, new Offset(i, position));
                keysMerged.add(key);
                int readSize = info.getValueSize(i);
                int realReadSize = info.input.read(buffer, 0, readSize);
                if (realReadSize < readSize) {
                    throw new RuntimeException();
                }

                output.write(buffer, 0, readSize);
                position += readSize;
                if (compare <= 0) {
                    ++i1;
                }
                if (compare >= 0) {
                    ++i2;
                }
            }
        }
        sstablesKeys.set(i, keysMerged.toArray(new KeyInfo[keysMerged.size()]));
        sstablesKeys.remove(i + 1);
        sstableFiles.set(i, new BufferedRandomAccessFile(getTableFile(i)));
        sstableFiles.remove(i + 1);

        Files.move(tempFile.toPath(), getTableFile(i).toPath(), StandardCopyOption.REPLACE_EXISTING);
        for (int j = i + 2; j < sstableFiles.size(); j++) {
            Files.move(getTableFile(j).toPath(), getTableFile(j - 1).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.delete(getTableFile(sstableFiles.size() - 1).toPath());
    }

    /*private void mergeTwoTableFiles(File file1, File file2, File result) throws IOException {
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

    private KeyValueInputStream<K, V> getTableInputStream(File tableFile) throws IOException {
        return new KeyValueInputStream<>(tableFile, keySerializationStrategy, valueSerializationStrategy);
    }

    private KeyValueOutputStream<K, V> getTableOutputStream(File tableFile, int numberEntries) throws IOException {
        return new KeyValueOutputStream<>(tableFile, keySerializationStrategy, valueSerializationStrategy, numberEntries);
    }*/

    /*private void mergeAllTableFiles() throws IOException {
        if (keys.isEmpty()) {
            for (int i = 0; i < numberTablesOnDisk; i++) {
                Files.delete(getTableFile(i).toPath());
            }
            sstableFiles.clear();
            return;
        }

        pushNewEntriesToDisk();
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
    }*/

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
        V cacheValue = cache.getIfPresent(key);
        if (cacheValue != null) {
            return cacheValue;
        }

        Offset offset = keys.get(key);
        assert offset != null && offset.fileIndex != -1;
        BufferedRandomAccessFile bufferedRandomAccessFile = sstableFiles.get(offset.fileIndex);
        V value;
        try {
            bufferedRandomAccessFile.seek(offset.offsetInFile);
            value = valueSerializationStrategy.deserializeFromStream(bufferedRandomAccessFile.dataInputStream);
            cache.put(key, value);
        } catch (IOException e) {
            throw new RuntimeException("Can't read from one of table files", e);
        }
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
        newEntries.put(key, value);
        try {
            checkForNewEntriesSize();
        } catch (IOException e) {
            throw new RuntimeException("Произошла IOException. В идеале она должна быть проброшена дальше как checked, но ...", e);
        }
    }

    @Override
    public synchronized void delete(K key) {
        checkForClosed();
        keys.remove(key);
        cache.invalidate(key);
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
        while (sstableFiles.size() > 1) {
            mergeTwoTableFiles(sstableFiles.size() - 2);
        }
        writeAllKeys();
        lock.release();
        isClosed = true;
    }
}
