package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Morell on 30.10.2016.
 */
public class SSTable<K, V> {
    private Serializator<K> kSerializator;
    private Serializator<V> valSerializator;
    private String filePath;
    private boolean closed;
    // В index загружается файл
    private final HashMap<K, V> index = new HashMap<>();
    private File data;

    public SSTable(String path, Serializator<K> keySer, Serializator<V> valSer) throws IOException {
        File check = new File(path);
        kSerializator = keySer;
        valSerializator = valSer;
        if (check.exists() && check.isDirectory()) {
            filePath = path + "/database.txt";
            data = new File(filePath);
        } else {
            throw new IOException();
        }
        if (data.exists()) {
            desirialize(data);
        }
    }

    private void serialize(File file) throws IOException {
        DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

        output.writeUTF("Myfile");
        int size = index.size();

        output.writeInt(size);

        for (Map.Entry<K, V> entry : index.entrySet()) {
            kSerializator.write(output, entry.getKey());
            valSerializator.write(output, entry.getValue());
        }
        output.close();
    }

    private void desirialize(File file) throws IOException {
        DataInputStream input = new DataInputStream(new FileInputStream(file));
        String check = input.readUTF();
        if (check.equals("Myfile")) {
            int sizeBase = input.readInt();
            for (int i = 0; i < sizeBase; i++) {
                K key = kSerializator.read(input);
                V value = valSerializator.read(input);
                index.put(key, value);
            }
            input.close();
        } else {
            input.close();
            throw new IOException("Invalid dataBase!");
        }
    }

    public V read(K key) throws IllegalStateException {
        isClosed();
        if (index.containsKey(key)) {
            return index.get(key);
        } else {
            return null;
        }
    }

    public boolean exists(K key) throws IllegalStateException {
        isClosed();
        return index.containsKey(key);
    }

    public void write(K key, V value) throws IllegalStateException {
        isClosed();
        index.put(key, value);
    }

    public Iterator readKeys() {
        isClosed();
        return index.keySet().iterator();
    }

    public void delete(K key) {
        index.remove(key);
    }

    public int size() {
        return index.size();
    }

    public void isClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Illegal work with closed file");
        }
    }

    public void close() throws IOException {
        if (!closed) {
            serialize(data);
            closed = true;
        } else {
            return;
        }
    }
}
