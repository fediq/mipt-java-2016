package ru.mipt.java2016.homework.g595.kryloff.task3;

import ru.mipt.java2016.homework.g595.kryloff.task2.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kryloff Gregory
 * @since 30.10.16
 */
public class JMyKVStorageV2<K, V> implements KeyValueStorage<K, V> {

    private static final Integer MAX_CACHE_SIZE = 100;
    private Map<K, V> cacheWrote;
    private Map<K, V> cacheRead;
    private Map<K, Integer> filePtr;
    private boolean isClosed;
    private JMySerializerInterface<K> keySerializer;
    private JMySerializerInterface<V> valueSerializer;
    private Integer filesNumber;
    private String path;

    private String generateFileName(Integer number) {
        return number.toString().concat(".db");
    }

    public JMyKVStorageV2(String pathArguement, JMySerializerInterface<K> keySerializerArguement,
            JMySerializerInterface<V> valueSerializerArguement) throws IOException {
        isClosed = false;
        filesNumber = 0;
        path = pathArguement;
        keySerializer = keySerializerArguement;
        valueSerializer = valueSerializerArguement;
        cacheWrote = new HashMap<>();
        cacheRead = new HashMap<>();
        Map<K, V> map;
        filePtr = new HashMap<>();
        while (true) {
            String fileName = generateFileName(filesNumber);
            File file = new File(pathArguement, fileName);
            if (file.exists()) {
                map = getData(fileName);
                System.gc();
                filesNumber++;
                for (Map.Entry<K, V> entry : map.entrySet()) {
                    if (!filePtr.containsKey(entry.getKey())) {
                        filePtr.put(entry.getKey(), filesNumber - 1);
                    }
                }
            } else {
                break;
            }
        }
        //System.out.println("End of constructor");
    }

    private Map<K, V> getData(String fileName) throws IOException {
        File file;
        try {
            file = new File(path, fileName);
        } catch (Exception ex) {
            System.out.println("Cannot create File");
            throw new RuntimeException();
        }
        if (!file.exists()) {
            System.out.println("File does not exist");
            throw new RuntimeException("File does not exist");
        }
        DataInputStream inputStream;

        K currentKey;
        V currentValue;
        Map<K, V> map = new HashMap<>();
        int count;
        int hash;
        try {
            inputStream = new DataInputStream(new FileInputStream(file));
            count = inputStream.readInt();
            hash = inputStream.readInt();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read from stream or crate it");
        }

        for (int i = 0; i < count; ++i) {
            currentKey = keySerializer.deSerialize(inputStream);
            currentValue = valueSerializer.deSerialize(inputStream);
            map.put(currentKey, currentValue);
        }
        if (hash != map.hashCode()) { //hashes are not equal
            throw new RuntimeException("File has been changed");
        }
        return map;

    }

    private void writeData(Map<K, V> map, Integer descriptor) throws IOException {
        File file;
        String fileName = generateFileName(descriptor);
        try {
            file = new File(path, fileName);
        } catch (Exception ex) {
            System.out.println("Cannot create File");
            throw new RuntimeException();
        }
        DataOutputStream outputStream;
        try {
            outputStream = new DataOutputStream(new FileOutputStream(file));
            outputStream.writeInt(map.size());
            outputStream.writeInt(map.hashCode());
        } catch (NullPointerException ex) {
            System.out.println("Cannot write to stream or create it" + file.getAbsolutePath());
            throw new RuntimeException("Cannot write to stream or create it");
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            try {
                filePtr.put(entry.getKey(), descriptor);
            } catch (Exception ex) {
                System.out.println("No desriptor for file");
            }
            keySerializer.serialize(outputStream, entry.getKey());
            valueSerializer.serialize(outputStream, entry.getValue());

        }
    }

    private V findInFile(K key, String fileName) throws IOException {
        File file = new File(path, fileName);
        DataInputStream inputStream;

        K currentKey;
        V currentValue;
        int count;
        try {
            inputStream = new DataInputStream(new FileInputStream(file));
            count = inputStream.readInt();
            inputStream.readInt();
        } catch (Exception ex) {
            System.out.println("Cannot read from stream or create it");
            throw new RuntimeException("Cannot read from stream or create it");
        }
        for (int i = 0; i < count; ++i) {
            currentKey = keySerializer.deSerialize(inputStream);
            currentValue = valueSerializer.deSerialize(inputStream);
            if (currentKey.equals(key)) {
                return currentValue;
            }

        }

        return null;
    }

    @Override
    public V read(K key) {
        //System.out.println("reading");
        checkNotClosed();
        if (!exists(key)) {
            //System.out.println("Not exists");
            return null;
        }
        if (cacheWrote.containsKey(key)) {
            //System.out.println("read");
            return cacheWrote.get(key);
        }
        if (cacheRead.containsKey(key)) {
            //System.out.println("read");
            return cacheRead.get(key);
        }
        //System.out.println("kek");
        String fileName = generateFileName(filePtr.get(key));

        try {
            V answer = findInFile(key, fileName);
            cacheRead.put(key, answer);
            return answer;
        } catch (IOException ex) {
            Logger.getLogger(JMyKVStorageV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.gc();
        if (cacheRead.size() >= MAX_CACHE_SIZE) {
            cacheRead.clear();
        }
        System.gc();
        //System.out.println("read");
        return null;

    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return filePtr.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        //System.out.println("writing");
        checkNotClosed();
        deleteSafely(key);
        filePtr.put(key, -1);
        cacheWrote.put(key, value);
        //System.out.println("LOOL");
        if (cacheWrote.size() >= MAX_CACHE_SIZE) {

            try {
                writeData(cacheWrote, addNewFile());

            } catch (IOException ex) {
                Logger.getLogger(JMyKVStorageV2.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.gc();
            cacheWrote.clear();
            System.gc();

        }
        //System.out.println("wrote");
    }

    
    @Override
    public void delete(K key) {
        deleteSafely(key);
        filePtr.remove(key);
    }
    
    private void deleteSafely(K key) {
        checkNotClosed();
        if (!exists(key)) {
            return;
        }
        if (filePtr.containsKey(key) && filePtr.get(key) != -1) {
            String fileName = generateFileName(filePtr.get(key));
            Map<K, V> map = new HashMap<>();
            try {
                map = getData(fileName);
            } catch (IOException ex) {
                Logger.getLogger(JMyKVStorageV2.class.getName()).log(Level.SEVERE, null, ex);
            }
            map.remove(key);
            try {
                writeData(map, filePtr.get(key));
            } catch (IOException ex) {
                Logger.getLogger(JMyKVStorageV2.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        cacheRead.remove(key);
        cacheWrote.remove(key);
        

    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return filePtr.keySet().iterator();
    }

    @Override
    public int size() {
        return filePtr.keySet().size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        try {
            writeData(cacheWrote, addNewFile());
            cacheWrote = null;
        } catch (IOException ex) {
            Logger.getLogger(JMyKVStorageV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.gc();
        cacheRead = null;
        isClosed = true;
    }

    private void checkNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("Already closed");
        }
    }

    private Integer addNewFile() {
        filesNumber++;
        return filesNumber - 1;
    }
}
