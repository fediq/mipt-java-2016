package ru.mipt.java2016.homework.g594.anukhin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by clumpytuna on 29.10.16.
 */

public class KeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {


    private HashMap<K, V> map;
    private HashMap<K, Long> offsetTable;
    private boolean isOpen = false;
    private String validateString = "MyKeyValueStorageIsAwesome";
    private String pathToStorage;
    private String path;
    private String pathToConf;
    private File data;
    private Serializable<K> key;
    private Serializable<V> value;
    private int offsetSize;
    private int numberOfDel = 0;
    private File lockFile;
    private String lockFileName = "lock.db";
    private int sizeOfStorage = 0;

    public KeyValueStorageImpl(String path, Serializable<K> key, Serializable<V> value)  {

        isOpen = true;
        offsetTable = new HashMap<K, Long>();
        this.key = key;
        this.value = value;
        this.path = path;
        data = new File(path);
        pathToStorage = path + "/storage.txt";
        pathToConf = path + "/config.txt";
        String tmpPath = path;
        String lockFilePath = path + File.separator + lockFileName;
        lockFile = new File(lockFilePath);
        if (!lockFile.exists()) {
            try {
                lockFile.createNewFile();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create lock file!");
            }
        } else {
            throw new IllegalStateException("Somebody is working right now!");
        }

        if (!data.exists() || !data.isDirectory()) {
            throw new IllegalStateException("Path isn't available!");
        }

        tmpPath = path + "/config.txt";
        File conf = new File(tmpPath);

        if (conf.exists()) {
            try (DataInputStream confInput = new DataInputStream((new FileInputStream(conf)))) {
                /*
                if (!confInput.readUTF().equals(validateString)) {
                    throw new IllegalStateException("Invalid file");
                }
                */
                offsetSize = confInput.readInt();
                sizeOfStorage = offsetSize;
                K keyT;
                long offset;
                for (int i = 0; i < offsetSize; ++i) {
                    keyT = key.deserialize(confInput);
                    offset = confInput.readLong();
                    offsetTable.put(keyT, offset);
                }
                confInput.close();
            } catch (IOException e) {
                throw new ConcurrentModificationException("Can't read from file!");
            }
        } else {
            try {
                conf.createNewFile();
                try (DataOutputStream output = new DataOutputStream(new FileOutputStream(conf))) {
                    output.writeUTF(validateString);
                    output.close();
                } catch (IOException e) {
                    throw new IllegalStateException("Can't write to file");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            offsetSize = 0;
        }
    }

    @Override
    public V read(K keyI) {
        checkNotClosed();
        RandomAccessFile file = null;
        if (offsetTable.containsKey(keyI)) {
            long offset = offsetTable.get(keyI);
            try {
                file = new RandomAccessFile(pathToStorage, "r");
                file.seek(offset);
                return value.deserialize(file);
            } catch (IOException e) {
                e.getStackTrace();
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public boolean exists(K keyI) {
        checkNotClosed();
        return offsetTable.containsKey(keyI);
    }

    @Override
    public void write(K keyI, V valueI) {
        checkNotClosed();
        try {
            ++sizeOfStorage;
            File file = new File(pathToStorage);
            RandomAccessFile fileOut = new RandomAccessFile(file, "rw");
            fileOut.seek(file.length());
            key.serialize(fileOut, keyI);
            long tmp = fileOut.getFilePointer();
            offsetTable.put(keyI, fileOut.getFilePointer());
            value.serialize(fileOut, valueI);
            fileOut.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Override
    public void delete(K keyI) {
        checkNotClosed();
        ++numberOfDel;
        offsetTable.remove(keyI);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return offsetTable.keySet().iterator();
    }

    @Override
    public int size() {
        return offsetTable.size();
    }

    @Override

    public void close() throws IOException {
        if (!isOpen){
            return;
        }
        isOpen = false;
        FileOutputStream out = new FileOutputStream(pathToConf);
        DataOutputStream configOut = new DataOutputStream(out);
        configOut.writeInt(offsetTable.size());
        for (Entry<K,  Long> i : offsetTable.entrySet()) {
            key.serialize(configOut, i.getKey());
            configOut.writeLong(i.getValue());
        }
        if (lockFile.exists()) {
                lockFile.delete();
        }
        configOut.close();
        out.close();
    }

    private void checkNotClosed() {
        if (!isOpen) {
            throw new IllegalStateException("Already closed");
        }
    }
}
