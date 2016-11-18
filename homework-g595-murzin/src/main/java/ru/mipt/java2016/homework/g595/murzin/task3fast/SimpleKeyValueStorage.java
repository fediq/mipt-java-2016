package ru.mipt.java2016.homework.g595.murzin.task3fast;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.murzin.task3.MyException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dima on 18.11.16.
 */
public class SimpleKeyValueStorage<Key, Value> implements KeyValueStorage<Key, Value> {

    private static final String KEYS_FILE_NAME = "keys.dat";

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

    private File storageDirectory;
    private SerializationStrategy<Key> keySerializationStrategy;
    private SerializationStrategy<Value> valueSerializationStrategy;
    private Comparator<Key> comparator;
    private HashMap<Integer, RandomAccessFile> files = new HashMap<>();
    private HashMap<Key, KeyWrapper> keys = new HashMap<>();

    private volatile boolean isClosed;

    public SimpleKeyValueStorage(String path,
                                 SerializationStrategy<Key> keySerializationStrategy,
                                 SerializationStrategy<Value> valueSerializationStrategy,
                                 Comparator<Key> comparator) {
        storageDirectory = new File(path);
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        this.comparator = comparator;

        try {
            File[] allFiles = Files.list(storageDirectory.toPath()).map(Path::toFile).toArray(File[]::new);
            for (File file : allFiles) {
                String fileName = file.getName();
                if (isPowerOfTwo(fileName)) {
                    files.put(Integer.valueOf(fileName), new RandomAccessFile(file, "rw"));
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
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(keysFile))) {
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
        RandomAccessFile file = files.get(x);
        assert (file != null);
        try {
            file.seek(wrapper.getOffsetInFile());
            return valueSerializationStrategy.deserializeFromStream(file);
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
        RandomAccessFile file = files.get(x);
        try {
            if (file == null) {
                file = new RandomAccessFile(new File(storageDirectory, String.valueOf(x)), "rw");
                files.put(x, file);
            }
            long fileLength = file.length();
            file.seek(fileLength);
            file.write(bytes);
            keys.put(key, new KeyWrapper(bytes.length, fileLength));
        } catch (IOException e) {
            throw new MyException("");
        }
    }

    @Override
    public void delete(Key key) {
        checkForClosed();
        KeyWrapper wrapper = keys.get(key);
        int x = lowerboundPowerOfTwo(wrapper.getValueLength());
        RandomAccessFile file = files.get(x);
        assert file != null;
        try {
            long fileLength = file.length();
            if (wrapper.getOffsetInFile() < fileLength - x) {
                file.seek(fileLength - x);
                byte[] bytes = new byte[x];
                file.read(bytes);
                file.seek(wrapper.getOffsetInFile());
                file.write(bytes);
            }
            file.setLength(fileLength - x);
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
        for (RandomAccessFile file : files.values()) {
            file.close();
        }
        writeAllKeys();
        isClosed = true;
    }
}
