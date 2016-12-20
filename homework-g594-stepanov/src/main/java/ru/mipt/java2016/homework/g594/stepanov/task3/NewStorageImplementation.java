package ru.mipt.java2016.homework.g594.stepanov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.Adler32;

public class NewStorageImplementation<K, V> implements KeyValueStorage {

    private FileOperation fileOperation;
    private HashMap keyOffsets;
    private String directory;
    private String mainStorage;
    private String temporaryStorage;
    private String keyType;
    private String valueType;
    private File lockFile;
    private File hashFile;
    private boolean closed = false;
    private Adler32 adler;

    public NewStorageImplementation(
            String directory, String keyType, String valueType) throws MalformedDataException {
        //checkClosed();
        adler = new Adler32();
        this.directory = directory;
        lockFile = new File(directory + "/Lock.txt");
        hashFile = new File(directory + "/Hash.txt");
        while (lockFile.exists()) {
            throw new MalformedDataException("Storage busy!");
        }
        try {
            lockFile.createNewFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.keyType = keyType;
        this.valueType = valueType;
        StringBuilder sb = new StringBuilder(directory);
        sb.append("/Storage.db");
        mainStorage = sb.toString();
        sb = new StringBuilder(directory);
        sb.append("/TemporaryStorage.db");
        temporaryStorage = sb.toString();
        FileOperationFactory factory = new FileOperationFactory(mainStorage, keyType, valueType);
        fileOperation = factory.getFileOperation();
        keyOffsets = factory.getMap();
        while (fileOperation.hasNext()) {
            Object key = fileOperation.readCurrentKey();
            Long currentOffset = fileOperation.getCurrentInputOffset();
            Object value = fileOperation.readCurrentValue();
            keyOffsets.put(key, currentOffset);
            adler.update(key.toString().getBytes());
            adler.update(value.toString().getBytes());
        }
        if (!hashFile.exists()) {
            try {
                hashFile.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            PrintWriter outputStream = null;
            try {
                outputStream = new PrintWriter(new FileOutputStream(hashFile));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            int curr = (int) adler.getValue();
            outputStream.print(new Integer(curr));
            outputStream.close();
        }
        BufferedReader inputStream = null;
        Integer hash = 0;
        try {
            inputStream = new BufferedReader(new FileReader(hashFile));
            String s = inputStream.readLine();
            hash = Integer.parseInt(s);
            inputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        int curr = (int) adler.getValue();
        if (!hash.equals(new Integer(curr))) {
            throw new MalformedDataException("Hash doesn't match");
        }
        fileOperation.setToStart();
    }

    @Override
    public Object read(Object key) {
        checkClosed();
        fileOperation.flush();
        if (keyOffsets.containsKey(key)) {
            Long offset = (Long) (keyOffsets.get(key));
            return fileOperation.readCertainValue(offset);
        } else {
            return null;
        }
    }

    @Override
    public boolean exists(Object key) {
        checkClosed();
        return keyOffsets.containsKey(key);
    }

    @Override
    public void write(Object key, Object value) {
        checkClosed();
        fileOperation.appendKey(key);
        Long offset = fileOperation.appendValue(value);
        keyOffsets.put(key, offset);
    }

    @Override
    public void delete(Object key) {
        checkClosed();
        if (keyOffsets.containsKey(key)) {
            keyOffsets.remove(key);
        }
    }

    @Override
    public Iterator readKeys() {
        checkClosed();
        return keyOffsets.keySet().iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return keyOffsets.size();
    }

    @Override
    public void flush() {
        checkClosed();
        adler = new Adler32();
        FileOperationFactory factory = new FileOperationFactory(temporaryStorage, keyType, valueType);
        FileOperation temporaryFileOperation = factory.getFileOperation();
        fileOperation.setToStart();
        while (fileOperation.hasNext()) {
            Object key = fileOperation.readCurrentKey();
            Long currentOffset = fileOperation.getCurrentInputOffset();
            Object value = fileOperation.readCurrentValue();
            if (keyOffsets.containsKey(key) && keyOffsets.get(key).equals(currentOffset)) {
                temporaryFileOperation.appendKey(key);
                temporaryFileOperation.appendValue(value);
                adler.update(key.toString().getBytes());
                adler.update(value.toString().getBytes());
            }
        }
        temporaryFileOperation.close();
        fileOperation.close();
        File storage = new File(mainStorage);
        storage.delete();
        File temporary = new File(temporaryStorage);
        temporary.renameTo(new File(mainStorage));
        factory = new FileOperationFactory(mainStorage, keyType, valueType);
        fileOperation = factory.getFileOperation();
        hashFile.delete();
        try {
            hashFile.createNewFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        PrintWriter outputStream = null;
        try {
            outputStream = new PrintWriter(new FileOutputStream(hashFile, true));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        outputStream.print((int) adler.getValue());
        outputStream.close();
    }

    @Override
    public void close() throws IOException {
        checkClosed();
        flush();
        closed = true;
        lockFile.delete();
    }

    void checkClosed() {
        if (closed) {
            throw new RuntimeException("Already closed");
        }
    }
}
