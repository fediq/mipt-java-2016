package ru.mipt.java2016.homework.g595.nosareva.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.nosareva.task2.Serializer;
import ru.mipt.java2016.homework.g595.nosareva.task2.SerializerForInteger;
import ru.mipt.java2016.homework.g595.nosareva.task2.SerializerForLong;
import ru.mipt.java2016.homework.g595.nosareva.task2.SerializerForString;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by maria on 18.11.16.
 */
public class OptimizedKVStorage<K, V> implements KeyValueStorage<K, V> {

    private final String directory;
    private final String validationString = "MadeByOptimizedKVStorage_v2.0";
    private final String initFileName = "KVStorage";
    private final String keysFileName = initFileName + "Keys";
    private final String valuesFileName = initFileName + "Values";

    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    static final Object LOCK_OBJECT = new Object();
    private final FileLock lockFile;
    private File initFile;
    private File values;
    private File keys;
    private RandomAccessFile keysFile;
    private RandomAccessFile valuesFile;
    private final HashMap<K, Long> keysOffsetsTable = new HashMap<>();
    private boolean closed = false;
    private int badValues = 0;

    public void createStorage() throws IOException {
        keys = new File(keysFileName);
        values = new File(valuesFileName);

        keys.createNewFile();
        values.createNewFile();

        keysFile = new RandomAccessFile(keys, "rw");
        valuesFile = new RandomAccessFile(values, "rw");
    }

    public void openStorage() throws IOException, ValidationException {
        keys = new File(keysFileName);
        values = new File(valuesFileName);

        if (!keys.exists() || !values.exists()) {
            throw new IOException("Files don't exist");
        }

        keysFile = new RandomAccessFile(keys, "rw");
        valuesFile = new RandomAccessFile(values, "rw");

        getKeysAndOffsets();
    }

    public void getKeysAndOffsets() throws IOException, ValidationException {
        if (!(new SerializerForString()).deserializeFromStream(keysFile).equals(validationString)) {
            throw new ValidationException("It isn't a valid storage");
        }

        int numberOfKeys = (new SerializerForInteger()).deserializeFromStream(keysFile);
        badValues = (new SerializerForInteger()).deserializeFromStream(keysFile);
        for (int i = 0; i < numberOfKeys; i++) {
            K key = keySerializer.deserializeFromStream(keysFile);
            Long offset = (new SerializerForLong()).deserializeFromStream(keysFile);
            keysOffsetsTable.put(key, offset);
        }
    }

    public OptimizedKVStorage(String path,
                              Serializer<K> serializerForKeys,
                              Serializer<V> serializerForValues)
            throws IOException, ValidationException {

        synchronized (LOCK_OBJECT) {
            lockFile = new RandomAccessFile(initFileName + "_lock_", "rw").getChannel().lock();
        }

        this.directory = path;
        this.keySerializer = serializerForKeys;
        this.valueSerializer = serializerForValues;

        File receivedFile = new File(directory);
        if (receivedFile.exists() && receivedFile.isDirectory()) {
            this.initFile = new File(directory + File.separator + initFileName);
        } else {
            throw new RuntimeException("path" + directory + " isn't available");
        }

        if (initFile.exists()) {
            DataInputStream input = new DataInputStream(new FileInputStream(initFile));
            if (!(new SerializerForString()).deserializeFromStream(input).equals(validationString)) {
                throw new ValidationException("This file isn't a valid storage");
            }
            openStorage();
        } else {
            initFile.createNewFile();
            createStorage();
        }
    }

    @Override
    public V read(K key) {
        chekingForClosed();
        if (!keysOffsetsTable.containsKey(key)) {
            return null;
        }

        Long offset = keysOffsetsTable.get(key);
        try {
            valuesFile.seek(offset);
            return valueSerializer.deserializeFromStream(valuesFile);
        } catch (IOException excep) {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        chekingForClosed();
        return keysOffsetsTable.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        chekingForClosed();

        try {
            if (keysOffsetsTable.containsKey(key)) {
                keysOffsetsTable.replace(key, valuesFile.length());
            } else {
                keysOffsetsTable.put(key, valuesFile.length());

            }
            valuesFile.seek(valuesFile.length());
            valueSerializer.serializeToStream(value, valuesFile);
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        }
    }

    @Override
    public void delete(K key) {
        chekingForClosed();
        if (keysOffsetsTable.containsKey(key)) {
            keysOffsetsTable.remove(key);
            badValues++;
            if (badValues >= 3 * size()) {
                flush();
            }
        }
    }

    @Override
    public Iterator<K> readKeys() {
        chekingForClosed();
        return keysOffsetsTable.keySet().iterator();
    }

    @Override
    public int size() {
        return keysOffsetsTable.size();
    }

    @Override
    public void flush() {
        File temp = new File(initFileName + "temp");
        try {
            if (temp.exists()) {
                temp.delete();
                temp.createNewFile();
            } else {
                temp.createNewFile();
            }

            RandomAccessFile randomTemp = new RandomAccessFile(temp, "rw");

            for (K key : keysOffsetsTable.keySet()) {
                valuesFile.seek(keysOffsetsTable.get(key));
                V val = valueSerializer.deserializeFromStream(valuesFile);
                keysOffsetsTable.replace(key, randomTemp.length());
                valueSerializer.serializeToStream(val, randomTemp);
            }
            temp.renameTo(values);
            valuesFile.close();
            valuesFile = new RandomAccessFile(values, "rw");
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
        badValues = 0;
    }

    private void chekingForClosed() {
        if (closed) {
            throw new IllegalStateException("File has been closed");
        }
    }

    @Override
    public void close() throws IOException {
        chekingForClosed();
        DataOutputStream out = new DataOutputStream(new FileOutputStream(initFile));
        (new SerializerForString()).serializeToStream(validationString, out);
        out.close();


        keys.delete();
        keys.createNewFile();
        keysFile = new RandomAccessFile(keys, "rw");
        keysFile.seek(0);

        (new SerializerForString()).serializeToStream(validationString, keysFile);
        (new SerializerForInteger()).serializeToStream(keysOffsetsTable.size(), keysFile);
        (new SerializerForInteger()).serializeToStream(badValues, keysFile);
        for (K key : keysOffsetsTable.keySet()) {
            keySerializer.serializeToStream(key, keysFile);
            (new SerializerForLong()).serializeToStream(keysOffsetsTable.get(key), keysFile);
        }
        keysOffsetsTable.clear();

        lockFile.release();
        keysFile.close();
        valuesFile.close();
        closed = true;
    }
}
