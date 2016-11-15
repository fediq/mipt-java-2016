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
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by dima on 05.11.16.
 */
public class LSMStorage<Key, Value> implements KeyValueStorage<Key, Value> {

    private static final String KEYS_FILE_NAME = "keys.dat";
    private static final int MAX_VALUE_SIZE = 10 * 1024;
    private static final int MAX_RAM_SIZE = 64 * 1024 * 1024;
    private static final double NEW_ENTRIES_PERCENTAGE = 0.3051;
    private static final double CACHE_PERCENTAGE = NEW_ENTRIES_PERCENTAGE;
    private static final int MAX_NEW_ENTRIES_SIZE = (int) (MAX_RAM_SIZE * NEW_ENTRIES_PERCENTAGE / MAX_VALUE_SIZE);
    private static final int MAX_CACHE_SIZE = (int) (MAX_RAM_SIZE * CACHE_PERCENTAGE / MAX_VALUE_SIZE);

    private SerializationStrategy<Key> keySerializationStrategy;
    private SerializationStrategy<Value> valueSerializationStrategy;
    private final Comparator<Key> comparator;
    private FileLock lock;
    private File storageDirectory;

    private Map<Key, KeyWrapper<Key, Value>> keys = new HashMap<>();
    private ArrayList<KeyWrapper<Key, Value>[]> sstablesKeys = new ArrayList<>();

    //    private Map<Key, Value> newEntries = new HashMap<>(MAX_NEW_ENTRIES_SIZE);
    private ArrayList<Key> newEntries = new ArrayList<>(MAX_NEW_ENTRIES_SIZE);

    private Cache<Key, Value> cache = CacheBuilder
            .newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .build();
    private ArrayList<BufferedRandomAccessFile> sstableFiles = new ArrayList<>();
    private volatile boolean isClosed;

    public LSMStorage(String path,
                      SerializationStrategy<Key> keySerializationStrategy,
                      SerializationStrategy<Value> valueSerializationStrategy,
                      Comparator<Key> comparator) {
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
            KeyWrapper<Key, Value>[] wrappers = keys.values().toArray(new KeyWrapper[keys.size()]);
            Arrays.sort(wrappers, (wrapper1, wrapper2) -> comparator.compare(wrapper1.key, wrapper2.key));
            sstablesKeys.add(wrappers);
            try {
                sstableFiles.add(new BufferedRandomAccessFile(getTableFile(0)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Кажется вы умудрились удалить файл базы сразу после того, " +
                        "как мы проверили, что он существует...", e);
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
                Key key = keySerializationStrategy.deserializeFromStream(input);
                KeyWrapper<Key, Value> wrapper = new KeyWrapper<>(key, input.readInt(), input.readLong());
                keys.put(key, wrapper);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read from keys file " + keysFile.getAbsolutePath(), e);
        }
    }

    private void writeAllKeys() throws IOException {
        File keysFile = new File(storageDirectory, KEYS_FILE_NAME);
        if (keys.isEmpty()) {
            Files.deleteIfExists(keysFile.toPath());
            return;
        }
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(keysFile)))) {
            output.writeInt(keys.size());
            for (Map.Entry<Key, KeyWrapper<Key, Value>> entry : keys.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                KeyWrapper<Key, Value> wrapper = entry.getValue();
                output.writeInt(wrapper.fileIndex);
                output.writeLong(wrapper.offsetInFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write to keys file " + keysFile.getAbsolutePath(), e);
        }
    }

    private void checkForNewEntriesSize() throws IOException {
        if (newEntries.size() < MAX_NEW_ENTRIES_SIZE) {
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

        KeyWrapper<Key, Value>[] wrappers = new KeyWrapper[newEntries.size()];
        Collections.sort(newEntries, comparator);
        try (FileOutputStream fileOutput = new FileOutputStream(newTableFile);
             FileChannel fileChannel = fileOutput.getChannel();
             DataOutputStream output = new DataOutputStream(fileOutput)) {
            output.writeInt(newEntries.size());
            for (int i = 0; i < newEntries.size(); i++) {
                Key key = newEntries.get(i);
                KeyWrapper<Key, Value> wrapper = keys.get(key);
                wrappers[i] = wrapper;
                wrapper.fileIndex = newTableIndex;
                wrapper.offsetInFile = fileChannel.position();
                valueSerializationStrategy.serializeToStream(wrapper.value, output);
                wrapper.value = null;
            }
            sstableFiles.add(new BufferedRandomAccessFile(getTableFile(newTableIndex)));
        } catch (IOException e) {
            throw new RuntimeException("Can't write to one of table files " + newTableFile.getAbsolutePath(), e);
        }
        sstablesKeys.add(wrappers);
        newEntries.clear();
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
            System.out.printf("%50s    ", Arrays.toString(tablesLength));
            mergeTwoTableFiles(n);
            System.out.println(Arrays.toString(sstablesKeys.stream().mapToInt(table -> table.length).toArray()));
        }
    }

    private class MergeInfo {
        public final int length;
        private final KeyWrapper<Key, Value>[] wrappers;
        private final FileInputStream input;
        private final long fileLength;

        MergeInfo(int i) throws IOException {
            wrappers = sstablesKeys.get(i);
            length = wrappers.length;
            input = new FileInputStream(getTableFile(i));
            int lengthRecordedInFile = new DataInputStream(input).readInt();
            if (lengthRecordedInFile != wrappers.length) {
                throw new IOException("TODO");
            }
            fileLength = sstableFiles.get(i).fileLength();
        }

        public Key keyAt(int i) {
            return wrappers[i].key;
        }

        public int getValueSize(int i) {
            long end = i == length - 1 ? fileLength : wrappers[i + 1].offsetInFile;
            long start = wrappers[i].offsetInFile;
            return (int) (end - start);
        }

        public int getMaxValueSize() {
            int maxSize = 0;
            for (int i = 0; i < length; i++) {
                maxSize = Math.max(maxSize, getValueSize(i));
            }
            return maxSize;
        }

        @Override
        public String toString() {
            return "MergeInfo{" +
                    "length=" + length +
                    ", wrappers=" + Arrays.toString(wrappers) +
                    '}';
        }
    }

    // Сливает таблицы i и i + 1
    // Полученной таблице присваивается номер i
    // Кроме того индексы всех таблиц с номерами j > i + 1 уменьшаются на один
    private void mergeTwoTableFiles(int iFile) throws IOException {
        MergeInfo info1 = new MergeInfo(iFile);
        MergeInfo info2 = new MergeInfo(iFile + 1);

        int maxValueSize = Math.max(info1.getMaxValueSize(), info2.getMaxValueSize());
        byte[] buffer = new byte[maxValueSize];

        ArrayList<KeyWrapper<Key, Value>> wrappersMerged = new ArrayList<>(info1.length + info2.length);
        File tempFile = Files.createTempFile("temp_table", "dat").toFile();
        try (FileOutputStream output = new FileOutputStream(tempFile)) {
            new DataOutputStream(output).writeInt(info1.length + info2.length); // Это число перезапишется позже
            int i1 = 0;
            int i2 = 0;
            long position = 4;
            while (i1 < info1.length || i2 < info2.length) {
                Key key1 = i1 == info1.length ? null : info1.keyAt(i1);
                Key key2 = i2 == info2.length ? null : info2.keyAt(i2);
                int compare = key1 == null ? 1 : key2 == null ? -1 : comparator.compare(key1, key2);
                int i = compare < 0 ? i1++ : i2++;
                Key key = compare < 0 ? key1 : key2;
                MergeInfo info = compare < 0 ? info1 : info2;

                KeyWrapper<Key, Value> wrapper = keys.get(key);
                if (wrapper != null) {
                    int readSize = info.getValueSize(i);
                    int realReadSize = info.input.read(buffer, 0, readSize);
                    if (realReadSize != readSize) {
                        throw new RuntimeException("TODO");
                    }
                    output.write(buffer, 0, readSize);
                    wrapper.fileIndex = iFile;
                    wrapper.offsetInFile = position;
                    wrappersMerged.add(wrapper);
                    position += readSize;
                } else {
                    int readSize = info.getValueSize(i);
                    long realReadSize = info.input.skip(readSize);
                    if (realReadSize != readSize) {
                        throw new RuntimeException("TODO");
                    }
                }

                if (compare == 0) {
                    int numberBytesToSkip = info1.getValueSize(i1);
                    long numberBytesSkipped = info1.input.skip(numberBytesToSkip);
                    if (numberBytesSkipped != numberBytesToSkip) {
                        throw new RuntimeException("TODO");
                    }
                    ++i1;
                }
            }
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw")) {
            randomAccessFile.writeInt(wrappersMerged.size());
        }

        Files.move(tempFile.toPath(), getTableFile(iFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
        for (int j = iFile + 2; j < sstableFiles.size(); j++) {
            Files.move(getTableFile(j).toPath(), getTableFile(j - 1).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.delete(getTableFile(sstableFiles.size() - 1).toPath());

        sstablesKeys.set(iFile, wrappersMerged.toArray(new KeyWrapper[wrappersMerged.size()]));
        sstablesKeys.remove(iFile + 1);
        sstableFiles.set(iFile, new BufferedRandomAccessFile(getTableFile(iFile)));
        sstableFiles.remove(iFile + 1);

        String[] filesRigth = IntStream.range(0, sstableFiles.size()).mapToObj(i -> "table" + i + ".dat").toArray(String[]::new);
        String[] filesReal = Files.list(storageDirectory.toPath()).map(Path::getFileName).map(Path::toString).filter(s -> s.charAt(0) == 't').sorted().toArray(String[]::new);
        assert Arrays.deepEquals(filesReal, filesRigth);
    }

    private void checkForClosed() {
        if (isClosed) {
            throw new RuntimeException("Access to closed storage");
        }
    }

    @Override
    public synchronized Value read(Key key) {
        checkForClosed();
        KeyWrapper<Key, Value> wrapper = keys.get(key);
        if (wrapper == null) {
            return null;
        }
        if (wrapper.value != null) {
            return wrapper.value;
        }
        Value cacheValue = cache.getIfPresent(key);
        if (cacheValue != null) {
            return cacheValue;
        }

        assert wrapper.fileIndex != -1;
        BufferedRandomAccessFile bufferedRandomAccessFile = sstableFiles.get(wrapper.fileIndex);
        Value value;
        try {
            bufferedRandomAccessFile.seek(wrapper.offsetInFile);
            value = valueSerializationStrategy.deserializeFromStream(bufferedRandomAccessFile.getDataInputStream());
            cache.put(key, value);
        } catch (IOException e) {
            throw new RuntimeException("Can't read from one of table files", e);
        }
        return value;
    }

    @Override
    public synchronized boolean exists(Key key) {
        checkForClosed();
        return keys.containsKey(key);
    }

    @Override
    public synchronized void write(Key key, Value value) {
        checkForClosed();
        KeyWrapper<Key, Value> wrapper = keys.get(key);
        if (wrapper == null) {
            keys.put(key, new KeyWrapper<>(key, value, newEntries.size()));
            newEntries.add(key);
        } else {
            wrapper.fileIndex = -1;
            wrapper.offsetInFile = -1;
            wrapper.indexInNewEntries = newEntries.size();
            if (wrapper.value == null) { // то есть если key не содержится в newEntries
                newEntries.add(key);
            }
        }
        cache.put(key, value);
        try {
            checkForNewEntriesSize();
        } catch (IOException e) {
            throw new RuntimeException("Произошла IOException. " +
                    "В идеале она должна быть проброшена дальше как checked, но ...", e);
        }
    }

    @Override
    public synchronized void delete(Key key) {
        checkForClosed();
        KeyWrapper<Key, Value> wrapper = keys.remove(key);
        if (wrapper.value != null) {
            wrapper.value = null;
            wrapper.indexInNewEntries = -1;
            Key lastNewEntryKey = newEntries.remove(newEntries.size() - 1);
            if (lastNewEntryKey != key) {
                newEntries.set(wrapper.indexInNewEntries, lastNewEntryKey);
                keys.get(lastNewEntryKey).indexInNewEntries = wrapper.indexInNewEntries;
            }
        }
        cache.invalidate(key);
    }

    @Override
    public synchronized Iterator<Key> readKeys() {
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
        // TODO если остался один файл -- удалить из него удалённые ключи
        checkForClosed();
        pushNewEntriesToDisk();
        while (sstableFiles.size() > 1) {
            mergeTwoTableFiles(sstableFiles.size() - 2);
        }
        writeAllKeys();
        lock.release();
        isClosed = true;
    }
}
