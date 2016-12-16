package ru.mipt.java2016.homework.g594.ishkhanyan.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g594.ishkhanyan.task2.MySerialization;
import ru.mipt.java2016.homework.g594.ishkhanyan.task2.Serializations;

/**
 * Created by ${Semien}
 * on ${20.11.16}.
 */

public class MyImprovedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final Object obj = new Object();
    private String keyType;
    private String valueType;
    private int maxSize = 900;
    private int fileNameEnd = 1;
    private long endOfFile = 0;
    private HashMap<K, V> newAdditions = new HashMap<>();
    private HashMap<K, Long> pathToValue = new HashMap<>();
    private MySerialization keySerializer;
    private MySerialization valueSerializer;
    private RandomAccessFile storageFile;
    private boolean fileIsNotEmpty; // true files have already been written
    private String pathDirectory;
    private String pathToConfigurations; // file with configurations
    private int numOfAdditions; // how many write operations have been done
    private int numOfDeletions; // how many deletions(or update) have been done
    private boolean isOpen;

    public MyImprovedKeyValueStorage(String path, MySerialization keySer,
                                     String keyT, MySerialization valueSer, String valueT) {
        pathDirectory = path;
        keySerializer = keySer;
        valueSerializer = valueSer;
        keyType = keyT;
        valueType = valueT;
        try {
            prepareToWork();
        } catch (IOException e) {
            throw new MalformedDataException(e.getMessage());
        }
    }

    public MyImprovedKeyValueStorage(String path, String keyType, String valueType) {
        pathDirectory = path;
        this.keyType = keyType;
        this.valueType = valueType;
        try {
            keySerializer = Serializations.takeSerializer(keyType);
            valueSerializer = Serializations.takeSerializer(valueType);
            prepareToWork();
        } catch (Exception e) {
            throw new MalformedDataException(e.getMessage());
        }
    }

    @Override
    public V read(K key) {
        synchronized (obj) {
            closeInspection();
            if (pathToValue.containsKey(key)) {
                if (newAdditions.containsKey(key)) {
                    return newAdditions.get(key);
                } else {
                    long index = pathToValue.get(key);
                    try {
                        storageFile.seek(index);
                        return (V) valueSerializer.readFromFile(storageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new MalformedDataException(e.getMessage());
                    }
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (obj) {
            closeInspection();
            return pathToValue.containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (obj) {
            closeInspection();
            checkSizeAndCorruption();
            if (fileIsNotEmpty) {
                ++numOfAdditions;
            }
            if (pathToValue.containsKey(key) && fileIsNotEmpty) {
                ++numOfDeletions;
            }
            pathToValue.put(key, (long) 0);
            newAdditions.put(key, value);
        }
    }

    @Override
    public void delete(K key) {
        synchronized (obj) {
            closeInspection();
            if (fileIsNotEmpty) {
                ++numOfDeletions;
            }
            pathToValue.remove(key);
            newAdditions.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (obj) {
            closeInspection();
            return pathToValue.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (obj) {
            closeInspection();
            return pathToValue.size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (obj) {
            closeInspection();
            recordNewAdditionToFile();
            storageFile.close();
            writeToConfig();
            isOpen = false;
        }
    }

    private void prepareToWork() throws IOException {
        synchronized (obj) {
            File directory = new File(pathDirectory);
            if (!directory.isDirectory()) {
                throw new IOException("invalid directory path");
            }
            if (!directory.exists()) {
                throw new IOException("such directory does not exist");
            }
            pathToConfigurations = pathDirectory + File.separator + "configs";
            File configs = new File(pathToConfigurations);
            if (!configs.exists()) {
                configs.createNewFile();
                storageFile = new RandomAccessFile(intToPath(fileNameEnd), "rw");
            } else {
                FileInputStream in = new FileInputStream(pathToConfigurations);
                DataInputStream configIn = new DataInputStream(in);
                fileIsNotEmpty = true;
                String s = configIn.readUTF();
                if (s.equals("lock")) {
                    throw new MalformedDataException("Storage has already been launched");
                }
                int numKeys = configIn.readInt();
                numOfAdditions = configIn.readInt();
                numOfDeletions = configIn.readInt();
                fileNameEnd = configIn.readInt();
                String keyT = configIn.readUTF();
                String valueT = configIn.readUTF();
                if (!keyType.equals(keyT) && !valueType.equals(valueT)) {
                    throw new MalformedDataException("invalid type");
                }
                K key;
                long index;
                for (int i = 0; i < numKeys; ++i) {
                    key = (K) keySerializer.readFromFile(configIn);
                    index = configIn.readLong();
                    pathToValue.put(key, index);
                }
                configIn.close();
                in.close();
                FileOutputStream out = new FileOutputStream(pathToConfigurations);
                DataOutputStream configOut = new DataOutputStream(out);
                configOut.writeUTF("lock");
                configOut.close();
                out.close();
                storageFile = new RandomAccessFile(intToPath(fileNameEnd), "rw");
            }
            isOpen = true;
        }
    }

    private void closeInspection() {
        if (!isOpen) {
            throw new MalformedDataException("Storage has already been closed");
        }
    }

    private void checkSizeAndCorruption() {
        if (newAdditions.size() >= maxSize) {
            try {
                recordNewAdditionToFile();
            } catch (IOException e) {
                throw new MalformedDataException("Error while creating a new file");
            }
        }
        if (fileIsNotEmpty && numOfAdditions < 2 * numOfDeletions
                && numOfDeletions > 20 * maxSize) {
            try {
                rebuild();
            } catch (IOException e) {
                throw new MalformedDataException("rebuilding error");
            }
        }
    }

    private void recordNewAdditionToFile() throws IOException {
        synchronized (obj) {
            fileIsNotEmpty = true;
            if (endOfFile != 0) {
                storageFile.seek(endOfFile);
            }
            for (Entry<K, V> i : newAdditions.entrySet()) {
                keySerializer.writeToFile(i.getKey(), storageFile);
                pathToValue.put(i.getKey(), storageFile.getFilePointer());
                valueSerializer.writeToFile(i.getValue(), storageFile);
            }
            newAdditions.clear();
        }
    }

    private String intToPath(int number) {
        return new String(pathDirectory + File.separator + Integer.toString(number));
    }

    private void rebuild() throws IOException { // record all information to another file
        int oldEnd = fileNameEnd;
        if (fileNameEnd == 1) {
            fileNameEnd = 2;
        } else {
            fileNameEnd = 1;
        }
        File newFile = new File(intToPath(fileNameEnd));
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        RandomAccessFile fileOut = new RandomAccessFile(intToPath(fileNameEnd), "rw");
        for (K key : pathToValue.keySet()) {
            keySerializer.writeToFile(key, fileOut);
            long point = fileOut.getFilePointer();
            pathToValue.put(key, point);
        }
        File oldStor = new File(intToPath(oldEnd));
        oldStor.delete();
        storageFile = fileOut;
    }

    private void writeToConfig() throws IOException {
        FileOutputStream out = new FileOutputStream(pathToConfigurations);
        DataOutputStream configOut = new DataOutputStream(out);
        configOut.writeUTF("ready");
        configOut.writeInt(pathToValue.size());
        configOut.writeInt(numOfAdditions);
        configOut.writeInt(numOfDeletions);
        configOut.writeInt(fileNameEnd);
        configOut.writeUTF(keyType);
        configOut.writeUTF(valueType);
        for (Entry<K, Long> i : pathToValue.entrySet()) {
            keySerializer.writeToFile(i.getKey(), configOut);
            configOut.writeLong(i.getValue());
        }
        configOut.close();
        out.close();
    }
}
