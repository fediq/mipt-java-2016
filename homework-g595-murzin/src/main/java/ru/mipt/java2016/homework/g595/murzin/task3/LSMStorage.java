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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by dima on 05.11.16.
 */
public class LSMStorage<Key, Value> implements KeyValueStorage<Key, Value> {

    private static final String KEYS_FILE_NAME = "keys.dat";
    private static final String INFO_FILE_NAME = "info.dat";
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
    private ArrayList<SstableInfo<Key, Value>> sstableInfos = new ArrayList<>();
    private HashSet<Integer> sstablesFilesIndexes = new HashSet<>();

    private ArrayList<Key> newEntries = new ArrayList<>(MAX_NEW_ENTRIES_SIZE);

    private Cache<Key, Value> cache = CacheBuilder
            .newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .build();
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
        readSstablesInfo();
    }

    private int getNextSstableFileIndex() {
        int i = 0;
        while (sstablesFilesIndexes.contains(i)) {
            ++i;
        }
        sstablesFilesIndexes.add(i);
        return i;
    }

    private String getTableFileName(int index) {
        return "table" + index + ".dat";
    }

    private File getSstableFile(int index) {
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
            throw new MyException("Can't read from keys file " + keysFile.getAbsolutePath(), e);
        }
    }

    private void writeAllKeys() throws IOException {
        File keysFile = new File(storageDirectory, KEYS_FILE_NAME);
        if (keys.isEmpty()) {
            Files.deleteIfExists(keysFile.toPath());
            return;
        }
        try (DataOutputStream output =
                     new DataOutputStream(new BufferedOutputStream(new FileOutputStream(keysFile)))) {
            output.writeInt(keys.size());
            for (Map.Entry<Key, KeyWrapper<Key, Value>> entry : keys.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                KeyWrapper<Key, Value> wrapper = entry.getValue();
                output.writeInt(wrapper.getTableIndex());
                output.writeLong(wrapper.getOffsetInFile());
            }
        }
    }

    private void readSstablesInfo() {
        File infoFile = new File(storageDirectory, INFO_FILE_NAME);
        boolean exists = infoFile.exists();
        if (exists == keys.isEmpty()) {
            throw new MyException("TODO");
        }
        if (!exists) {
            return;
        }

        Map<Integer, List<KeyWrapper<Key, Value>>> collect =
                keys.values().stream().collect(Collectors.groupingBy(KeyWrapper::getTableIndex));
        try (DataInputStream input = new DataInputStream(new FileInputStream(infoFile))) {
            int n = input.readInt();
            if (!IntStream.range(0, n).boxed().collect(Collectors.toSet()).equals(collect.keySet())) {
                throw new MyException("TODO");
            }
            for (int i = 0; i < n; i++) {
                int fileIndex = input.readInt();
                List<KeyWrapper<Key, Value>> wrappersList = collect.get(i);
                KeyWrapper<Key, Value>[] wrappers = wrappersList.toArray(new KeyWrapper[wrappersList.size()]);
                Arrays.sort(wrappers, (wrapper1, wrapper2) -> comparator.compare(wrapper1.key, wrapper2.key));
                sstableInfos.add(new SstableInfo<>(fileIndex, getSstableFile(fileIndex), wrappers));
                sstablesFilesIndexes.add(fileIndex);
            }
        } catch (IOException e) {
            throw new MyException("TODO", e);
        }
    }

    private void writeSstablesInfo() throws IOException {
        File infoFile = new File(storageDirectory, INFO_FILE_NAME);
        if (sstableInfos.isEmpty()) {
            return;
        }
        try (DataOutputStream output =
                     new DataOutputStream(new BufferedOutputStream(new FileOutputStream(infoFile)))) {
            output.writeInt(sstableInfos.size());
            for (SstableInfo<Key, Value> info : sstableInfos) {
                output.writeInt(info.fileIndex);
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
        int newTableFileIndex = getNextSstableFileIndex();
        File newTableFile = getSstableFile(newTableFileIndex);
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
                wrapper.setTableIndex(sstableInfos.size());
                wrapper.setOffsetInFile(fileChannel.position());
                valueSerializationStrategy.serializeToStream(wrapper.getValue(), output);
                wrapper.setValue(null);
            }
        } catch (IOException e) {
            throw new MyException("Can't write to one of table files " + newTableFile.getAbsolutePath(), e);
        }
        sstableInfos.add(new SstableInfo<>(newTableFileIndex, newTableFile, wrappers));
        newEntries.clear();
    }

    private void checkForMergeTablesOnDisk() throws IOException {
        // https://habrahabr.ru/post/251751/
        while (sstableInfos.size() > 1) {
            int n = sstableInfos.size() - 2;
            int[] tablesLength = sstableInfos.stream().mapToInt(table -> table.size).toArray();
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
            System.out.println(Arrays.toString(sstableInfos.stream().mapToInt(table -> table.size).toArray()));
        }
    }

    private class MergeInfo {
        public final int length;
        private final SstableInfo<Key, Value> sstableInfo;
        private final InputStream input;
        private final long fileLength;

        MergeInfo(int i) throws IOException {
            sstableInfo = sstableInfos.get(i);
            length = sstableInfo.size;
            input = new BufferedInputStream(new FileInputStream(sstableInfo.file), MAX_NEW_ENTRIES_SIZE / 3);
            int lengthRecordedInFile = new DataInputStream(input).readInt();
            if (lengthRecordedInFile != sstableInfo.size) {
                throw new MyException("TODO");
            }
            fileLength = sstableInfos.get(i).getBufferedRandomAccessFile().fileLength();
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
    private void mergeTwoTableFiles(int iTable) throws IOException {
        MergeInfo info1 = new MergeInfo(iTable);
        MergeInfo info2 = new MergeInfo(iTable + 1);

        int maxValueSize = Math.max(info1.getMaxValueSize(), info2.getMaxValueSize());
        byte[] buffer = new byte[maxValueSize];

        ArrayList<KeyWrapper<Key, Value>> wrappersMerged = new ArrayList<>(info1.length + info2.length);
        int newFileIndex = getNextSstableFileIndex();
        File newFile = getSstableFile(newFileIndex);
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(newFile), MAX_NEW_ENTRIES_SIZE / 2)) {
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
                    wrapper.setTableIndex(iTable);
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
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(newFile, "rw")) {
            randomAccessFile.writeInt(wrappersMerged.size());
        }

        Files.delete(info1.sstableInfo.file.toPath());
        Files.delete(info2.sstableInfo.file.toPath());
        sstableInfos.remove(info1.sstableInfo);
        sstableInfos.remove(info2.sstableInfo);
        sstableInfos.add(iTable, new SstableInfo<>(
                newFileIndex, newFile, wrappersMerged.toArray(new KeyWrapper[wrappersMerged.size()])));
        for (int i = iTable + 1; i < sstableInfos.size(); i++) {
            for (Key key : sstableInfos.get(i).keys) {
                KeyWrapper<Key, Value> wrapper = keys.get(key);
                wrapper.setTableIndex(wrapper.getTableIndex() - 1);
            }
        }
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

        try {
            assert wrapper.getTableIndex() != -1;
            BufferedRandomAccessFile bufferedRandomAccessFile =
                    sstableInfos.get(wrapper.getTableIndex()).getBufferedRandomAccessFile();
            Value value;
            bufferedRandomAccessFile.seek(wrapper.getOffsetInFile());
            value = valueSerializationStrategy.deserializeFromStream(bufferedRandomAccessFile.getDataInputStream());
            cache.put(key, value);
            return value;
        } catch (IOException e) {
            throw new MyException("Can't read from one of table files", e);
        }
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
            wrapper.setTableIndex(-1);
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
        checkForClosed();
        pushNewEntriesToDisk();
        writeAllKeys();
        writeSstablesInfo();
        for (SstableInfo<Key, Value> info : sstableInfos) {
            info.getBufferedRandomAccessFile().close();
        }
        lock.release();
        isClosed = true;
    }
}
