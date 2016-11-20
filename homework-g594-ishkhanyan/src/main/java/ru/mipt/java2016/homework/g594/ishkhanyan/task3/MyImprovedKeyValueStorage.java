package ru.mipt.java2016.homework.g594.ishkhanyan.task3;

import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g594.ishkhanyan.task2.MySerialization;
import ru.mipt.java2016.homework.g594.ishkhanyan.task2.Serializations;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by ${Semien}
 * on ${20.11.16}.
 */

public class MyImprovedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private String keyType;
    private String valueType;
    private int maxSize = 700;
    private HashMap<K, V> newAdditions = new HashMap<>();
    private HashMap<K, Pair<Integer, Long>> pathToValue = new HashMap<>(); // keys and pair (number of file and index)
    private Set<K> usedKeys = new HashSet<>();
    private MySerialization keySerializer;
    private MySerialization valuSerializer;
    private boolean fileIsNotEmpty; // true files have already been written
    private String pathDirectory;
    private String pathToConfigurations; // file with configurations
    private int numOfAdditions; // how many write operations have been done after the first record to disk
    private int numOfDeletions; // how many deletions(or update) have been done after the first record to disk
    private int numberOfCurrentFile;
    private int numberOfFirstFile;
    private boolean isOpen;

    public MyImprovedKeyValueStorage(String path, MySerialization keySer, String keyT,
                                     MySerialization valueSer, String valueT) {
        pathDirectory = path;
        keySerializer = keySer;
        valuSerializer = valueSer;
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
            valuSerializer = Serializations.takeSerializer(valueType);
            prepareToWork();
        } catch (Exception e) {
            throw new MalformedDataException(e.getMessage());
        }
    }

    @Override
    public V read(K key) {
        closeInspection();
        if (usedKeys.contains(key)) {
            if (newAdditions.containsKey(key)) {
                return newAdditions.get(key);
            } else {
                int numOfFile = pathToValue.get(key).getKey();
                long index = pathToValue.get(key).getValue();
                RandomAccessFile file = null;
                try {
                    file = new RandomAccessFile(intToPath(numOfFile), "r");
                    file.seek(index);
                    return (V) valuSerializer.readFromFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MalformedDataException(e.getMessage());
                } finally {
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new MalformedDataException(e.getMessage());
                        }
                    }
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        closeInspection();
        return usedKeys.contains(key);
    }

    @Override
    public void write(K key, V value) {
        closeInspection();
        checkSizeAndCorruption();
        if (fileIsNotEmpty) {
            ++numOfAdditions;
        }
        if (usedKeys.contains(key) && fileIsNotEmpty) {
            ++numOfDeletions;
        }
        usedKeys.add(key);
        newAdditions.put(key, value);
    }

    @Override
    public void delete(K key) {
        closeInspection();
        if (fileIsNotEmpty) {
            ++numOfDeletions;
        }
        usedKeys.remove(key);
        pathToValue.remove(key);
        newAdditions.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        closeInspection();
        return usedKeys.iterator();
    }

    @Override
    public int size() {
        closeInspection();
        return usedKeys.size();
    }

    @Override
    public void close() throws IOException {
        closeInspection();
        rebuild();
        writeToConfig();
    }

    private void prepareToWork() throws IOException {
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
        } else {
            FileInputStream in = new FileInputStream(pathToConfigurations);
            DataInputStream configIn = new DataInputStream(in);
            int numKeys = configIn.readInt();
            numOfAdditions = configIn.readInt();
            numOfDeletions = configIn.readInt();
            numberOfFirstFile = configIn.readInt();
            numberOfCurrentFile = configIn.readInt();
            String keyT = configIn.readUTF();
            String valueT = configIn.readUTF();
            if (!keyType.equals(keyT) && !valueType.equals(valueT)) {
                throw new MalformedDataException("invalid type");
            }
            K key;
            int numOfFile;
            long index;
            for (int i = 0; i < numKeys; ++i) {
                key = (K) keySerializer.readFromFile(configIn);
                numOfFile = configIn.readInt();
                index = configIn.readLong();
                usedKeys.add(key);
                pathToValue.put(key, new Pair(numOfFile, index));
            }
            configIn.close();
            in.close();
        }
        isOpen = true;
    }

    private void closeInspection() {
        if (!isOpen) {
            throw new MalformedDataException("Storage has already been closed");
        }
    }

    private void checkSizeAndCorruption() {
        if (newAdditions.size() > maxSize) {
            try {
                recordNewAdditionToFile();
            } catch (IOException e) {
                throw new MalformedDataException("Error while creating a new file");
            }
        }
//            if(fileIsNotEmpty && numOfAdditions < 2*numOfDeletions && numOfDeletions > maxSize){
//                try {
//                    rebuild();
//                } catch (IOException e) {
//                    throw new MalformedDataException("rebuilding error");
//                }
//            }
    }

    private void recordNewAdditionToFile() throws IOException {
        File newFile = new File(intToPath(numberOfCurrentFile));
        if (newFile.exists()) {
            throw new MalformedDataException("file have already been created");
        } else {
            RandomAccessFile fileOut = new RandomAccessFile(intToPath(numberOfCurrentFile), "rw");
            for (Entry<K, V> i : newAdditions.entrySet()) {
                keySerializer.writeToFile(i.getKey(), fileOut);
                pathToValue.put(i.getKey(), new Pair(numberOfCurrentFile, fileOut.getFilePointer()));
                valuSerializer.writeToFile(i.getValue(), fileOut);
            }
        }
        newAdditions.clear();
        ++numberOfCurrentFile;
    }

    private String intToPath(int number) {
        return new String(pathDirectory + File.separator + Integer.toString(number));
    }

    private void rebuild() throws IOException { // record all information to one file and delete others
        int numOfNewFile;
        if (numberOfFirstFile > 0) {
            numOfNewFile = 0;
        } else {
            numOfNewFile = numberOfCurrentFile;
        }
        File newFile = new File(intToPath(numOfNewFile));
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        RandomAccessFile fileOut = new RandomAccessFile(intToPath(numOfNewFile), "rw");
        for (K key : usedKeys) {
            keySerializer.writeToFile(key, fileOut);
            pathToValue.put(key, new Pair(numOfNewFile, fileOut.getFilePointer()));
            valuSerializer.writeToFile(read(key), fileOut);
        }
        for (int i = numberOfFirstFile; i < numberOfCurrentFile; ++i) {
            File curFile = new File(intToPath(i));
            curFile.delete();
        }
        numberOfFirstFile = numOfNewFile;
        numberOfCurrentFile = numOfNewFile + 1;
    }

    private void writeToConfig() throws IOException {
        FileOutputStream out = new FileOutputStream(pathToConfigurations);
        DataOutputStream configOut = new DataOutputStream(out);
        configOut.writeInt(usedKeys.size());
        configOut.writeInt(numOfAdditions);
        configOut.writeInt(numOfDeletions);
        configOut.writeInt(numberOfFirstFile);
        configOut.writeInt(numberOfCurrentFile);
        configOut.writeUTF(keyType);
        configOut.writeUTF(valueType);
        for (Entry<K, Pair<Integer, Long>> i : pathToValue.entrySet()) {
            keySerializer.writeToFile(i.getKey(), configOut);
            configOut.writeInt(i.getValue().getKey());
            configOut.writeLong(i.getValue().getValue());
        }
        configOut.close();
        out.close();
    }
}
