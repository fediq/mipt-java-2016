package ru.mipt.java2016.homework.g595.murzin.task3fast;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.murzin.task3.MyException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dima on 18.11.16.
 */
public class FunnyStorage<Key, Value> implements KeyValueStorage<Key, Value> {

    private static final String KEYS_FILE_NAME = "keys.dat";
    private static final int MAX_RAM_SIZE = 64 * 1024 * 1024;
    private static final double CACHE_PERCENTAGE = 0.3052 / 2;
    private static final int MAX_CACHE_SIZE = (int) (MAX_RAM_SIZE * CACHE_PERCENTAGE);

    private static boolean isPowerOfTwo(String s) {
        try {
            int x = Integer.valueOf(s);
            while (x >= 2 && x % 2 == 0) {
                x /= 2;
            }
            return x == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int lowerboundPowerOfTwo(int length) {
        int x = 1;
        while (x < length) {
            x *= 2;
        }
        return x;
    }

    private class FileInfo implements Closeable, Flushable {
        public final RandomAccessFile file;
        private ByteArrayOutputStream cache;

        public FileInfo(RandomAccessFile file) {
            this.file = file;
        }

        public void write(byte[] bytes) throws IOException {
            if (cache == null) {
                cache = new ByteArrayOutputStream();
            }
            cache.write(bytes);
            summaryCacheSize += bytes.length;
            if (summaryCacheSize > MAX_CACHE_SIZE) {
                flush();
            }
        }

        @Override
        public void flush() throws IOException {
            if (cache == null) {
                return;
            }
            file.write(cache.toByteArray());
            summaryCacheSize -= cache.size();
            cache.reset();
        }

        @Override
        public void close() throws IOException {
            flush();
            file.close();
        }
    }

    private File storageDirectory;
    private SerializationStrategy<Key> keySerializationStrategy;
    private SerializationStrategy<Value> valueSerializationStrategy;
    private HashMap<Integer, FileInfo> files = new HashMap<>();
    private HashMap<Key, KeyWrapper> keys = new HashMap<>();
    private int summaryCacheSize;
    private volatile boolean isClosed;

    public FunnyStorage(String path,
                        SerializationStrategy<Key> keySerializationStrategy,
                        SerializationStrategy<Value> valueSerializationStrategy) {
        storageDirectory = new File(path);
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        try {
            File[] allFiles = Files.list(storageDirectory.toPath()).map(Path::toFile).toArray(File[]::new);
            for (File file : allFiles) {
                String fileName = file.getName();
                if (isPowerOfTwo(fileName)) {
                    files.put(Integer.valueOf(fileName), new FileInfo(new RandomAccessFile(file, "rw")));
                }
            }
            readAllKeys();
        } catch (IOException e) {
            throw new MyException("");
        }
    }

    private void readAllKeys() {
        File keysFile = new File(storageDirectory, KEYS_FILE_NAME);
        if (!keysFile.exists()) {
            return;
        }
        try (DataInputStream input = new DataInputStream(new FileInputStream(keysFile))) {
            int n = input.readInt();
            for (int i = 0; i < n; i++) {
                Key key = keySerializationStrategy.deserializeFromStream(input);
                KeyWrapper wrapper = new KeyWrapper(input.readInt(), input.readLong());
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
            for (Map.Entry<Key, KeyWrapper> entry : keys.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                KeyWrapper wrapper = entry.getValue();
                output.writeInt(wrapper.getValueLength());
                output.writeLong(wrapper.getOffsetInFile());
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
        KeyWrapper wrapper = keys.get(key);
        if (wrapper == null) {
            return null;
        }
        int x = lowerboundPowerOfTwo(wrapper.getValueLength());
        FileInfo info = files.get(x);
        assert (info != null);
        try {
            info.flush();
            info.file.seek(wrapper.getOffsetInFile());
            return valueSerializationStrategy.deserializeFromStream(info.file);
        } catch (IOException e) {
            throw new MyException("");
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            valueSerializationStrategy.serializeToStream(value, dataOutputStream);
        } catch (IOException e) {
            throw new MyException("");
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        int x = lowerboundPowerOfTwo(bytes.length);
        FileInfo info = files.get(x);
        try {
            if (info == null) {
                info = new FileInfo(new RandomAccessFile(new File(storageDirectory, String.valueOf(x)), "rw"));
                files.put(x, info);
            }
//            long fileLength = info.file.length();
//            info.file.seek(fileLength);
//            info.file.write(bytes);
            info.write(bytes);
            keys.put(key, new KeyWrapper(bytes.length, info.file.length()));
        } catch (IOException e) {
            throw new MyException("");
        }
    }

    @Override
    public void delete(Key key) {
        checkForClosed();
        KeyWrapper wrapper = keys.get(key);
        int x = lowerboundPowerOfTwo(wrapper.getValueLength());
        FileInfo info = files.get(x);
        assert info != null;
        try {
            info.flush();
            long fileLength = info.file.length();
            if (wrapper.getOffsetInFile() < fileLength - x) {
                info.file.seek(fileLength - x);
                byte[] bytes = new byte[x];
                info.file.read(bytes);
                info.file.seek(wrapper.getOffsetInFile());
                info.file.write(bytes);
            }
            info.file.setLength(fileLength - x);
        } catch (IOException e) {
            throw new MyException("");
        }
        keys.remove(key);
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
        for (FileInfo info : files.values()) {
            info.close();
        }
        writeAllKeys();
        isClosed = true;
    }
}
