package ru.mipt.java2016.homework.g595.gusarova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.gusarova.task2.SerializerAndDeserializer;
import ru.mipt.java2016.homework.g595.gusarova.task2.SerializerAndDeserializerForLong;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by Дарья on 20.11.2016.
 */
public class KVStorage<K, V> implements KeyValueStorage<K, V> {
    private RandomAccessFile fileWithKeysAndOffsets;
    private RandomAccessFile fileWithValues;
    private RandomAccessFile temperary;
    private final FileLock lockFile;
    private final RandomAccessFile fileForLock;
    static final Object LOCK_OBJECT = new Object();


    private SerializerAndDeserializer<K> serializerAndDeserializerForKey;
    private SerializerAndDeserializer<V> serializerAndDeserializerForValue;
    private SerializerAndDeserializer<Long> serializerAndDeserializerForOffsets;
    private HashMap<K, Long> map;
    private Boolean baseClosed = false;
    private Long maxOffset;
    private Integer counter = 0;
    private String validationString = "data is correct";

    private void addKeysAndOffsets() throws IOException {
        map = new HashMap<K, Long>();
        DataInput input = fileWithKeysAndOffsets;
        int size = input.readInt();
        maxOffset = input.readLong();
        counter = input.readInt();
        for (int i = 0; i < size; i++) {
            map.put(serializerAndDeserializerForKey.deserialize(input),
                    serializerAndDeserializerForOffsets.deserialize(input));
        }
    }

    public void validation() throws IOException {
        Integer coun = 0;
        if (fileWithKeysAndOffsets.length() == 0) {
            coun++;
        } else {
            DataInput input = fileWithKeysAndOffsets;
            fileWithKeysAndOffsets.seek(0);
            String str = input.readUTF();
            if (str.equals(validationString)) {
                coun++;
            }
        }
        if (fileWithValues.length() == 0) {
            coun++;
            DataOutput output = fileWithValues;
            fileWithValues.seek(0);
            output.writeUTF(validationString);
            maxOffset = fileWithValues.getFilePointer() + 1;
        } else {
            DataInput input = fileWithValues;
            fileWithValues.seek(0);
            String str = input.readUTF();
            if (str.equals(validationString)) {
                coun++;
            }
        }
        if (coun == 2) {
            return;
        } else {
            throw new IOException();
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
            synchronized (LOCK_OBJECT) {
                fileForLock = new RandomAccessFile(path + File.separator + "Lock.txt", "rw");
                lockFile = fileForLock.getChannel().lock();
            }

            fileWithKeysAndOffsets = new RandomAccessFile(path + File.separator + "KeysAndOffsets.txt", "rw");
            fileWithValues = new RandomAccessFile(path + File.separator + "Values.txt", "rw");
            temperary = new RandomAccessFile(path + File.separator + "Temperary.txt", "rw");
            fileWithValues.seek(0);
            maxOffset = fileWithValues.getFilePointer();
            try {
                validation();
            } catch (IOException exp) {
                throw new MalformedDataException("data was uncorrectly changed");
            }
        } catch (IOException e) {
            throw new MalformedDataException("problems with files");
        }
        serializerAndDeserializerForKey = forKey;
        serializerAndDeserializerForValue = forValue;
        serializerAndDeserializerForOffsets = new SerializerAndDeserializerForLong();
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
            if (map.containsKey(key)) {
                flush();
            }
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
            flush();
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


    @Override
    public void flush() {
        counter++;
        if (counter > 3 * map.size()) {
            try {
                counter = 0;
                DataOutput output = temperary;
                DataInput input = fileWithValues;
                temperary.seek(0);
                for (K entry : map.keySet()) {
                    fileWithValues.seek(map.get(entry));
                    V temp = serializerAndDeserializerForValue.deserialize(input);
                    map.replace(entry, temperary.getFilePointer());
                    serializerAndDeserializerForValue.serialize(temp, output);
                }
                DataOutput secondOutput = fileWithValues;
                DataInput secondInput = temperary;
                fileWithValues.seek(0);
                secondOutput.writeUTF(validationString);
                for (K entry : map.keySet()) {
                    temperary.seek(map.get(entry));
                    V temp = serializerAndDeserializerForValue.deserialize(secondInput);
                    map.replace(entry, fileWithValues.getFilePointer());
                    serializerAndDeserializerForValue.serialize(temp, secondOutput);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (baseClosed) {
            return;
        }
        try {
            temperary.close();
            fileWithValues.close();
            DataOutput output = fileWithKeysAndOffsets;
            fileWithKeysAndOffsets.seek(0);
            output.writeUTF(validationString);
            output.writeInt(map.size());
            output.writeLong(maxOffset);
            output.writeInt(counter);
            for (K entry : map.keySet()) {
                serializerAndDeserializerForKey.serialize(entry, output);
                serializerAndDeserializerForOffsets.serialize(map.get(entry), output);
            }

            fileWithKeysAndOffsets.close();
            baseClosed = true;
            lockFile.release();
            fileForLock.close();
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
    }
}
