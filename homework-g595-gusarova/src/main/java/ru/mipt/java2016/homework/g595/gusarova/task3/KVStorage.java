package ru.mipt.java2016.homework.g595.gusarova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by Дарья on 20.11.2016.
 */
public class KVStorage<K, V> implements KeyValueStorage<K, V> {
    private RandomAccessFile fileWithKeysAndOffsets;
    private RandomAccessFile fileWithValues;
    private SerializerAndDeserializer<K> serializerAndDeserializerForKey;
    private SerializerAndDeserializer<V> serializerAndDeserializerForValue;
    private SerializerAndDeserializer<Long> serializerAndDeserializerForOffsets;
    private HashMap<K, Long> map;
    private Boolean baseClosed = false;
    private Long maxOffset;

    private void addKeysAndOffsets() throws IOException {
        map = new HashMap<K, Long>();
        DataInput input = fileWithKeysAndOffsets;
        fileWithKeysAndOffsets.seek(0);
        int size = input.readInt();
        maxOffset = input.readLong();
        for (int i = 0; i < size; i++) {
            map.put(serializerAndDeserializerForKey.deserialize(input),
                    serializerAndDeserializerForOffsets.deserialize(input));
        }
    }

    public KVStorage(String path, SerializerAndDeserializer<K> forKey,
                     SerializerAndDeserializer<V> forValue) throws MalformedDataException {
        File f;
        f  = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            throw new MalformedDataException("this path is incorrect");
        }
        try {
            fileWithKeysAndOffsets = new RandomAccessFile(path + File.separator + "KeysAndOffsets.txt", "rw");
            fileWithValues = new RandomAccessFile(path + File.separator + "Values.txt", "rw");
            fileWithValues.seek(0);
            maxOffset = fileWithValues.getFilePointer();
        } catch (IOException e) {
            throw new MalformedDataException("problems with files");
        }
        serializerAndDeserializerForKey = forKey;
        serializerAndDeserializerForValue = forValue;
        serializerAndDeserializerForOffsets = new SerializersAndDeserializers.SerializerAndDeserializerForLong();
        try {
            addKeysAndOffsets();
        } catch (IOException exp) {
            //base was never printed on disk
        }
    }

    @Override
    public V read(K key) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        if (!map.containsKey(key)) {
            return null;
        }
        try {
            fileWithValues.seek(map.get(key));
            DataInput input = fileWithValues;
            V temp = serializerAndDeserializerForValue.deserialize(input);
            return temp;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        return map.containsKey(key);
    }


    @Override
    public void write(K key, V value) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        try {
            fileWithValues.seek(maxOffset);
            DataOutput output = fileWithValues;
            serializerAndDeserializerForValue.serialize(value, output);
            map.put(key, maxOffset);
            maxOffset = fileWithValues.getFilePointer() + 1;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(K key) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        if (map.containsKey(key)) {
            map.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        return map.size();
    }

    public void flush() { }

    @Override
    public void close() throws IOException {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        try {
            fileWithValues.close();
            DataOutput output = fileWithKeysAndOffsets;
            fileWithKeysAndOffsets.seek(0);
            output.writeInt(map.size());
            output.writeLong(maxOffset);
            for (K entry : map.keySet()) {
                serializerAndDeserializerForKey.serialize(entry, output);
                serializerAndDeserializerForOffsets.serialize(map.get(entry), output);
            }
            baseClosed = true;
            fileWithKeysAndOffsets.close();
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
    }
}
