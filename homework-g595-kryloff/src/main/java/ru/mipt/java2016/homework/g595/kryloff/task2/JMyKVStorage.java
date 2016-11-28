package ru.mipt.java2016.homework.g595.kryloff.task2;

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

/**
 * @author Kryloff Gregory
 * @since 30.10.16
 */
public class JMyKVStorage<K, V> implements KeyValueStorage<K, V> {

    private static final  String FILE_NAME = "storage.db";
    private Map<K, V> storageMap;
    private boolean isFileClosed;
    private JMySerializerInterface<K> keySerializer;
    private JMySerializerInterface<V> valueSerializer;
    private File file;

    public JMyKVStorage(String pathArguement, JMySerializerInterface<K> keySerializerArguement,
            JMySerializerInterface<V> valueSerializerArguement) throws IOException {
        isFileClosed = false;
        keySerializer = keySerializerArguement;
        valueSerializer = valueSerializerArguement;
        file = new File(pathArguement, FILE_NAME);
        storageMap = new HashMap<>();
        if (file.exists()) {
            
            getData();
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Cannot create file");
            }
            
        }
    }
                

    private void getData() throws IOException {
        DataInputStream inputStream;
        
        K currentKey;
        V currentValue;
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
            storageMap.put(currentKey, currentValue);
        }
        if (hash != storageMap.hashCode()) { //hashes are not equal
            throw new RuntimeException("File has been changed");
        }
            
    }

    private void writeData() throws IOException {
        DataOutputStream outputStream;
        try {
            outputStream =  new DataOutputStream(new FileOutputStream(file));
            outputStream.writeInt(storageMap.size());
            outputStream.writeInt(storageMap.hashCode());
        } catch (IOException ex) {
            throw new RuntimeException("Cannot write to stream or create it");
        }
        for (Map.Entry<K, V> entry : storageMap.entrySet()) {
            keySerializer.serialize(outputStream, entry.getKey());
            valueSerializer.serialize(outputStream, entry.getValue());
        }
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        return storageMap.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return storageMap.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        storageMap.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        storageMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return storageMap.keySet().iterator();
    }

    @Override
    public int size() {
        return storageMap.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        writeData();
        storageMap = null;
        isFileClosed = true;
    }

    private void checkNotClosed() {
        if (isFileClosed) {
            throw new IllegalStateException("Already closed");
        }
    }
}
