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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dima on 05.11.16.
 */
public class LSMStorage<Key, Value> implements KeyValueStorage<Key, Value> {

    private static final String KEYS_FILE_NAME = "keys.dat";
    private static final int MAX_VALUE_SIZE = 10 * 1024;
    private static final int MAX_RAM_SIZE = 64 * 1024 * 1024;
    private static final double NEW_ENTRIES_PERCENTAGE = 0.3052 / 2;
    private static final double CACHE_PERCENTAGE = NEW_ENTRIES_PERCENTAGE;
    private static final int MAX_NEW_ENTRIES_SIZE = (int) (MAX_RAM_SIZE * NEW_ENTRIES_PERCENTAGE / MAX_VALUE_SIZE);
    private static final int MAX_CACHE_SIZE = (int) (MAX_RAM_SIZE * CACHE_PERCENTAGE / MAX_VALUE_SIZE);

    private SerializationStrategy<Key> keySerializationStrategy;
    private SerializationStrategy<Value> valueSerializationStrategy;
    private final Comparator<Key> comparator;
    private FileLock lock;
    private File storageDirectory;

    private Map<Key, KeyWrapper<Key, Value>> keys = new HashMap<>();
    private ArrayList<SstableInfo<Key, Value>> sstablesKeys = new ArrayList<>();

    //    private Map<Key, Value> newEntries = new HashMap<>(MAX_NEW_ENTRIES_SIZE);
    private ArrayList<Key> newEntries = new ArrayList<>(MAX_NEW_ENTRIES_SIZE);

    private Cache<Key, Value> cache = CacheBuilder
            .newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .build();
    private ArrayList<BufferedRandomAccessFile> sstablesBufferedRandomAccessFiles = new ArrayList<>();
    private ArrayList<File> sstableFiles = new ArrayList<>();
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
            throw new MyException("Path " + path + " is not a valid directory name");
        }

        synchronized (LSMStorage.class) {
            try {
                // It is between processes lock!!!
                lock = new RandomAccessFile(new File(path, ".lock"), "rw").getChannel().lock();
            } catch (IOException e) {
                throw new MyException("Can't create lock file", e);
            }
        }

        readAllKeys();
        if (getTableFile(0).exists()) {
            KeyWrapper<Key, Value>[] wrappers = keys.values().toArray(new KeyWrapper[keys.size()]);
            Arrays.sort(wrappers, (wrapper1, wrapper2) -> comparator.compare(wrapper1.key, wrapper2.key));
            sstablesKeys.add(new SstableInfo<>(wrappers));
            try {
                sstablesBufferedRandomAccessFiles.add(new BufferedRandomAccessFile(getTableFile(0)));
            } catch (FileNotFoundException e) {
                throw new MyException("Кажется вы умудрились удалить файл базы сразу после того, " +
                        "как мы проверили, что он существует...", e);
            }
        }
    }

    private String getTableFileName(int index) {
        return "table" + index + ".dat";
    }

    private File getTableFile(int index) {
        while (index >= sstableFiles.size()) {
            sstableFiles.add(null);
        }
        if (sstableFiles.get(index) == null) {
            sstableFiles.set(index, new File(storageDirectory, getTableFileName(index)));
        }
        return sstableFiles.get(index);
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
            throw new MyException("Can't read from keys file " + keysFile.getAbsolutePath(), e);
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
                output.writeInt(wrapper.getFileIndex());
                output.writeLong(wrapper.getOffsetInFile());
            }
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
        int newTableIndex = sstablesBufferedRandomAccessFiles.size();
        File newTableFile = getTableFile(newTableIndex);
        try {
            Files.createFile(newTableFile.toPath());
        } catch (IOException e) {
            throw new MyException("Can't create one of table files " + newTableFile.getAbsolutePath(), e);
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
                wrapper.setFileIndex(newTableIndex);
                wrapper.setOffsetInFile(fileChannel.position());
                valueSerializationStrategy.serializeToStream(wrapper.getValue(), output);
                wrapper.setValue(null);
            }
            sstablesBufferedRandomAccessFiles.add(new BufferedRandomAccessFile(getTableFile(newTableIndex)));
        } catch (IOException e) {
            throw new MyException("Can't write to one of table files " + newTableFile.getAbsolutePath(), e);
        }
        sstablesKeys.add(new SstableInfo<>(wrappers));
        newEntries.clear();
    }

    private void checkForMergeTablesOnDisk() throws IOException {
        // https://habrahabr.ru/post/251751/
        while (sstablesBufferedRandomAccessFiles.size() > 1) {
            int n = sstablesBufferedRandomAccessFiles.size() - 2;
            int[] tablesLength = sstablesKeys.stream().mapToInt(table -> table.length).toArray();
            if (n > 0 && tablesLength[n - 1] <= tablesLength[n] + tablesLength[n + 1]
                    || n - 1 > 0 && tablesLength[n - 2] <= tablesLength[n] + tablesLength[n - 1]) {
                if (tablesLength[n - 1] < tablesLength[n + 1]) {
                    n--;
                }
            } else if (n < 0 || tablesLength[n] > tablesLength[n + 1]) {
                break; // инвариант установлен
            }
//            System.out.printf("%50s    ", Arrays.toString(tablesLength));
            mergeTwoTableFiles(n);
//            System.out.println(Arrays.toString(sstablesKeys.stream().mapToInt(table -> table.length).toArray()));
        }
    }

    private class MergeInfo {
        public final int length;
        private final SstableInfo<Key, Value> sstableInfo;
        private final FileInputStream input;
        private final long fileLength;

        MergeInfo(int i) throws IOException {
            sstableInfo = sstablesKeys.get(i);
            length = sstableInfo.length;
            input = new FileInputStream(getTableFile(i));
            int lengthRecordedInFile = new DataInputStream(input).readInt();
            if (lengthRecordedInFile != sstableInfo.length) {
                throw new MyException("TODO");
            }
            fileLength = sstablesBufferedRandomAccessFiles.get(i).fileLength();
        }

        public Key keyAt(int i) {
            return sstableInfo.keys[i];
        }

        public int getValueSize(int i) {
            long end = i == length - 1 ? fileLength : sstableInfo.valuesOffsets[i + 1];
            long start = sstableInfo.valuesOffsets[i];
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
                    ", sstableInfo=" + sstableInfo +
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
                        throw new MyException("TODO");
                    }
                    output.write(buffer, 0, readSize);
                    wrapper.setFileIndex(iFile);
                    wrapper.setOffsetInFile(position);
                    wrappersMerged.add(wrapper);
                    position += readSize;
                } else {
                    int readSize = info.getValueSize(i);
                    long realReadSize = info.input.skip(readSize);
                    if (realReadSize != readSize) {
                        throw new MyException("TODO");
                    }
                }

                if (compare == 0) {
                    int numberBytesToSkip = info1.getValueSize(i1);
                    long numberBytesSkipped = info1.input.skip(numberBytesToSkip);
                    if (numberBytesSkipped != numberBytesToSkip) {
                        throw new MyException("TODO");
                    }
                    ++i1;
                }
            }
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw")) {
            randomAccessFile.writeInt(wrappersMerged.size());
        }

        Files.move(tempFile.toPath(), getTableFile(iFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
        for (int j = iFile + 2; j < sstablesBufferedRandomAccessFiles.size(); j++) {
            Files.move(getTableFile(j).toPath(), getTableFile(j - 1).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.delete(getTableFile(sstablesBufferedRandomAccessFiles.size() - 1).toPath());

        sstablesKeys.set(iFile, new SstableInfo<>(wrappersMerged.toArray(new KeyWrapper[wrappersMerged.size()])));
        sstablesKeys.remove(iFile + 1);
        sstablesBufferedRandomAccessFiles.set(iFile, new BufferedRandomAccessFile(getTableFile(iFile)));
        sstablesBufferedRandomAccessFiles.remove(iFile + 1);
    }

    private void checkForClosed() {
        if (isClosed) {
            throw new RuntimeException("Access to closed storage");
        }
    }

    @Override
    public Value read(Key key) {
        checkForClosed();
        KeyWrapper<Key, Value> wrapper = keys.get(key);
        if (wrapper == null) {
            return null;
        }
        if (wrapper.getValue() != null) {
            return wrapper.getValue();
        }
        Value cacheValue = cache.getIfPresent(key);
        if (cacheValue != null) {
            return cacheValue;
        }

        assert wrapper.getFileIndex() != -1;
        BufferedRandomAccessFile bufferedRandomAccessFile =
                sstablesBufferedRandomAccessFiles.get(wrapper.getFileIndex());
        Value value;
        try {
            bufferedRandomAccessFile.seek(wrapper.getOffsetInFile());
            value = valueSerializationStrategy.deserializeFromStream(bufferedRandomAccessFile.getDataInputStream());
            cache.put(key, value);
        } catch (IOException e) {
            throw new MyException("Can't read from one of table files", e);
        }
        return value;
    }

    @Override
    public boolean exists(Key key) {
        checkForClosed();
        return keys.containsKey(key);
    }

    @Override
    public void write(Key key, Value value) {
        checkForClosed();
        KeyWrapper<Key, Value> wrapper = keys.get(key);
        if (wrapper == null) {
            keys.put(key, new KeyWrapper<>(key, value, newEntries.size()));
            newEntries.add(key);
        } else {
            wrapper.setFileIndex(-1);
            wrapper.setOffsetInFile(-1);
            wrapper.setIndexInNewEntries(newEntries.size());
            if (wrapper.getValue() == null) { // то есть если key не содержится в newEntries
                newEntries.add(key);
            }
            wrapper.setValue(value);
        }
        cache.put(key, value);
        try {
            checkForNewEntriesSize();
        } catch (IOException e) {
            throw new MyException("Произошла IOException. " +
                    "В идеале она должна быть проброшена дальше как checked, но ...", e);
        }
    }

    @Override
    public void delete(Key key) {
        checkForClosed();
        KeyWrapper<Key, Value> wrapper = keys.remove(key);
        if (wrapper.getValue() != null) {
            wrapper.setValue(null);
//            wrapper.fileIndex = -1;
//            wrapper.offsetInFile = -1;
//            wrapper.indexInNewEntries = -1;
            Key lastNewEntryKey = newEntries.remove(newEntries.size() - 1);
            if (lastNewEntryKey != key) {
                newEntries.set(wrapper.getIndexInNewEntries(), lastNewEntryKey);
                keys.get(lastNewEntryKey).setIndexInNewEntries(wrapper.getIndexInNewEntries());
            }
        }
        cache.invalidate(key);
    }

    @Override
    public Iterator<Key> readKeys() {
        checkForClosed();
        return keys.keySet().iterator();
    }

    @Override
    public int size() {
        checkForClosed();
        return keys.size();
    }

    @Override
    public void close() throws IOException {
        // TODO если остался один файл -- удалить из него удалённые ключи
        checkForClosed();
        pushNewEntriesToDisk();
        while (sstablesBufferedRandomAccessFiles.size() > 1) {
            mergeTwoTableFiles(sstablesBufferedRandomAccessFiles.size() - 2);
        }
        writeAllKeys();
        lock.release();
        isClosed = true;
    }
}
