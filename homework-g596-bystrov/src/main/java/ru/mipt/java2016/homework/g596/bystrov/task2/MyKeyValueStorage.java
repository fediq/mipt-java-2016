package ru.mipt.java2016.homework.g596.bystrov.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.util.HashMap;

/**
 * Created by AlexBystrov.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private HashMap<K, V> map = new HashMap<K, V>();
    private SerializationStrategy<K> ssk;
    private SerializationStrategy<V> ssv;
    private File f;
    private boolean open;

    public MyKeyValueStorage(SerializationStrategy<K> ssk, SerializationStrategy<V> ssv,
            String fileName) {
        this.ssk = ssk;
        this.ssv = ssv;
        File folder = new File(fileName);
        if (!folder.exists()) {
            throw new RuntimeException("Folder doesn't exist");
        }
        f = new File(folder, "storage.db");
        if (f.exists()) {
            readFile();
        }
        open = true;
    }


    private void readFile() {
        try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
            int k = in.readInt();
            for (int i = 0; i < k; i++) {
                K key = ssk.deserialize(in);
                V value = ssv.deserialize(in);
                map.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read file", e);
        }
    }

    private void writeFile() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(f))) {
            out.writeInt(map.size());
            for (Map.Entry<K, V> writeMap : map.entrySet()) {
                ssk.serialize(writeMap.getKey(), out);
                ssv.serialize(writeMap.getValue(), out);
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't write file", e);
        }
    }

    public V read(K key) {
        return map.get(key);
    }

    public void write(K key, V value) {
        if (!open) {
            throw new RuntimeException("File is closed");
        }
        map.put(key, value);
    }

    public Iterator<K> readKeys() {
        if (!open) {
            throw new RuntimeException("File is closed");
        }
        return map.keySet().iterator();
    }

    public void delete(K key) {
        map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public boolean exists(K key) {
        return map.containsKey(key);
    }

    public void close() throws IOException {
        writeFile();
        open = false;
    }
}
