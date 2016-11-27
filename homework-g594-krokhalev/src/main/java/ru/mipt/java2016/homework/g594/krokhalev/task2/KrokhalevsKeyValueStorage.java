package ru.mipt.java2016.homework.g594.krokhalev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.*;

import static java.io.File.separatorChar;

public class KrokhalevsKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String STORAGE_NAME = "storage.db";

    private int version = 0;

    private File storageFile;
    private File keysFile;
    private File valuesFile;

    private Serializer<K, V> serializer;

    public KrokhalevsKeyValueStorage(String workDirectoryName, Class<K> keyClass, Class<V> valueClass) {
        this(workDirectoryName, new StorageSerializer<K, V>(keyClass, valueClass));
    }

    public KrokhalevsKeyValueStorage(String workDirectoryName, Serializer serializer) {
        this.serializer = serializer;

        File workDirectory = new File(workDirectoryName);
        storageFile = null;

        if (workDirectory.exists() && workDirectory.isDirectory()) {
            for (File item : workDirectory.listFiles()) {
                if (item.isFile() && item.getName().equals(STORAGE_NAME)) {
                    storageFile = item;
                    break;
                }
            }

            try {

                keysFile = new File(workDirectory.getAbsolutePath() + separatorChar + "KEYS" + STORAGE_NAME);
                valuesFile = new File(workDirectory.getAbsolutePath() + separatorChar + "VALUES" + STORAGE_NAME);

                if (!keysFile.createNewFile() || !valuesFile.createNewFile()) {
                    throw new IOException();
                }

                if (storageFile == null) {
                    storageFile = new File(workDirectory.getAbsolutePath() + separatorChar + STORAGE_NAME);

                    if (!storageFile.createNewFile()) {
                        throw new IOException();
                    }

                } else {
                    restoreFromStorage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Wrong directory");
        }
    }

    private boolean readBool(RandomAccessFile file) throws IOException {
        byte[] boolBuff = new byte[1];
        file.read(boolBuff);
        return (boolean) serializer.deserialize(boolean.class, boolBuff);
    }

    private byte[] readBytes(RandomAccessFile file) throws IOException {
        int size = readInt(file);

        byte[] keyBuff = new byte[size];
        file.read(keyBuff);

        return keyBuff;
    }

    private int readInt(RandomAccessFile file) throws IOException {
        byte[] intBuff = new byte[4];
        file.read(intBuff);
        return (int) serializer.deserialize(int.class, intBuff);
    }

    private long readLong(RandomAccessFile file) throws IOException {
        byte[] longBuff = new byte[8];
        file.read(longBuff);
        return (long) serializer.deserialize(long.class, longBuff);
    }

    private K readKey(RandomAccessFile file) throws IOException {
        return serializer.deserializeKey(readBytes(file));
    }

    private V readValue(RandomAccessFile file) throws IOException {
        return serializer.deserializeValue(readBytes(file));
    }

    private void missNext(RandomAccessFile file) throws IOException {
        int size = readInt(file);

        file.seek(file.getFilePointer() + size);
    }

    private void findKey(RandomAccessFile file, K key) throws IOException {
        while (file.getFilePointer() < file.length()) {
            file.seek(file.getFilePointer() + 1);

            int size = readInt(file);

            byte[] keyBuff = new byte[size];
            file.read(keyBuff);

            K currKey = serializer.deserializeKey(keyBuff);

            if (key.equals(currKey)) {
                file.seek(file.getFilePointer() - size - 5);
                return;
            } else {
                file.seek(file.getFilePointer() + 8);
            }
        }
    }

    @Override
    public V read(K key) {
        if (!keysFile.exists()) {
            throw new RuntimeException();
        }

        V ans = null;
        try {
            RandomAccessFile keysRAFile = new RandomAccessFile(keysFile, "r");
            RandomAccessFile valuesRAFile = new RandomAccessFile(valuesFile, "r");

            findKey(keysRAFile, key);

            if (keysRAFile.getFilePointer() < keysRAFile.length()) {
                keysRAFile.seek(keysRAFile.getFilePointer() + 1);
                missNext(keysRAFile);

                long pos = readLong(keysRAFile);

                valuesRAFile.seek(pos);

                ans = readValue(valuesRAFile);
            }

            keysRAFile.close();
            valuesRAFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    @Override
    public boolean exists(K key) {
        if (!keysFile.exists()) {
            throw new RuntimeException();
        }

        boolean ans = false;
        try {
            RandomAccessFile file = new RandomAccessFile(keysFile, "r");

            findKey(file, key);

            if (file.getFilePointer() < file.length()) {
                ans = !readBool(file);
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    @Override
    public void write(K key, V value) {
        if (!keysFile.exists()) {
            throw new RuntimeException();
        }

        try {
            RandomAccessFile keysRAFile = new RandomAccessFile(keysFile, "rw");
            RandomAccessFile valuesRAFile = new RandomAccessFile(valuesFile, "rw");
            valuesRAFile.seek(valuesRAFile.length());

            findKey(keysRAFile, key);
            keysRAFile.write(serializer.serialize(false));
            if (keysRAFile.getFilePointer() < keysRAFile.length()) {

                missNext(keysRAFile);

            } else {
                byte[] keyBuff = null;

                keyBuff = serializer.serialize(key);

                keysRAFile.write(serializer.serialize(keyBuff.length));
                keysRAFile.write(keyBuff);

                version++;

            }
            keysRAFile.write(serializer.serialize(valuesRAFile.getFilePointer()));

            byte[] valueBuff = null;

            valueBuff = serializer.serialize(value);

            valuesRAFile.write(serializer.serialize(valueBuff.length));
            valuesRAFile.write(valueBuff);

            keysRAFile.close();
            valuesRAFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) {
        if (!keysFile.exists()) {
            throw new RuntimeException();
        }

        try {
            RandomAccessFile file = new RandomAccessFile(keysFile, "rw");

            findKey(file, key);
            if (file.getFilePointer() < file.length()) {
                version++;

                file.write(serializer.serialize(true));
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (!keysFile.exists()) {
            throw new RuntimeException();
        }

        try {
            return new Iterator<K>() {
                private long pos = 0;
                private int iterVersion = version;

                private boolean hasNext(RandomAccessFile file) {
                    try {
                        while (file.getFilePointer() < file.length()) {
                            boolean isDel = readBool(file);
                            if (isDel) {
                                missNext(file);
                                file.seek(file.getFilePointer() + 8);
                            } else {
                                file.seek(file.getFilePointer() - 1);
                                break;
                            }
                        }

                        return file.getFilePointer() < file.length();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean hasNext() {
                    if (version != iterVersion) {
                        throw new ConcurrentModificationException();
                    }

                    boolean ans = false;
                    try {
                        RandomAccessFile file = new RandomAccessFile(keysFile, "r");
                        file.seek(pos);

                        ans = hasNext(file);
                        pos = file.getFilePointer();

                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return ans;
                }

                @Override
                public K next() {
                    try {
                        RandomAccessFile file = new RandomAccessFile(keysFile, "r");
                        file.seek(pos);

                        if (hasNext(file)) {
                            readBool(file);
                            K key = readKey(file);
                            file.seek(file.getFilePointer() + 8);

                            pos = file.getFilePointer();
                            return key;
                        }

                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int size() {
        if (!keysFile.exists()) {
            throw new RuntimeException();
        }

        int ans = 0;

        try {
            RandomAccessFile file = new RandomAccessFile(keysFile, "r");

            while (file.getFilePointer() < file.length()) {
                boolean isDel = readBool(file);
                if (!isDel) {
                    ans++;
                }

                missNext(file);
                file.seek(file.getFilePointer() + 8);
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ans;
    }

    private void restoreFromStorage() throws IOException {
        RandomAccessFile storageRAFile = new RandomAccessFile(storageFile, "r");
        RandomAccessFile keysRAFile    = new RandomAccessFile(keysFile, "rw");
        RandomAccessFile valuesRAFile  = new RandomAccessFile(valuesFile, "rw");

        while (storageRAFile.getFilePointer() < storageRAFile.length()) {
            byte[] tmp = readBytes(storageRAFile);
            keysRAFile.write(serializer.serialize(false));
            keysRAFile.write(serializer.serialize(tmp.length));
            keysRAFile.write(tmp);
            keysRAFile.write(serializer.serialize(valuesRAFile.getFilePointer()));

            tmp = readBytes(storageRAFile);
            valuesRAFile.write(serializer.serialize(tmp.length));
            valuesRAFile.write(tmp);
        }

        storageRAFile.close();
        keysRAFile.close();
        valuesRAFile.close();
    }

    private void saveToStorage() throws IOException {
        if (storageFile.delete()) {
            if (!storageFile.createNewFile()) {
                throw new IOException();
            }

            RandomAccessFile storageRAFile = new RandomAccessFile(storageFile, "rw");
            RandomAccessFile keysRAFile = new RandomAccessFile(keysFile, "r");
            RandomAccessFile valuesRAFile = new RandomAccessFile(valuesFile, "r");

            while (keysRAFile.getFilePointer() < keysRAFile.length()) {
                boolean isDel = readBool(keysRAFile);



                if (!isDel) {
                    byte[] tmp = readBytes(keysRAFile);
                    storageRAFile.write(serializer.serialize(tmp.length));
                    storageRAFile.write(tmp);

                    long pos = readLong(keysRAFile);

                    valuesRAFile.seek(pos);
                    tmp = readBytes(valuesRAFile);
                    storageRAFile.write(serializer.serialize(tmp.length));
                    storageRAFile.write(tmp);
                } else {
                    missNext(keysRAFile);
                    keysRAFile.seek(keysRAFile.getFilePointer() + 8);
                }
            }

            keysRAFile.close();
            valuesRAFile.close();
            storageRAFile.close();

        } else {
            throw new IOException();
        }
    }

    @Override
    public void close() throws IOException {
        saveToStorage();

        if (!keysFile.delete() || !valuesFile.delete()) {
            throw new IOException();
        }
    }
}
