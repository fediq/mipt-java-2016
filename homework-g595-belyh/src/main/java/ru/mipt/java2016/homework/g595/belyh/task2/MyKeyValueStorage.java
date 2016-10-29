package ru.mipt.java2016.homework.g595.belyh.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.*;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by white2302 on 29.10.2016.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private HashMap <K, V> map = new HashMap<>();
    private boolean closed;
    private Serializer<K> KeySerializer;
    private Serializer<V> ValueSerializer;
    private String myPath;

    MyKeyValueStorage(String Path, Serializer<K> SerializerK, Serializer<V> SerializerV) throws IOException {
        File f = new File(Path + "/db.txt");
        myPath = Path + "/db.txt";
        KeySerializer = SerializerK;
        ValueSerializer = SerializerV;

        closed = false;

        if (!f.exists()) {
            f.createNewFile();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
            out.writeUTF("MyDataBase");
            out.close();
            return;
        }

        DataInputStream in = new DataInputStream(new FileInputStream((f)));
        String check = in.readUTF();

        if (!check.equals("MyDataBase")) {
            throw new RuntimeException("It's not my database!");
        }

        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            map.put(KeySerializer.deserialize(in), ValueSerializer.deserialize(in));
        }
        in.close();
    }

    private void isClosed() {
        if (closed) {
            throw new RuntimeException("Database is closed");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return map.keySet().iterator();
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return map.containsKey(key);
    }

    @Override
    public void close() throws IOException {
        isClosed();
        closed = true;

        File f = new File(myPath);

        if (!f.exists()) {
            f.createNewFile();
        }

        DataOutputStream out = new DataOutputStream(new FileOutputStream(f));

        out.writeUTF("MyDataBase");
        out.writeInt(map.size());

        for (HashMap.Entry<K, V> it : map.entrySet()) {
            KeySerializer.serialize(it.getKey(), out);
            ValueSerializer.serialize(it.getValue(), out);
        }
        out.close();
    }

    @Override
    public int size() {
        isClosed();
        return map.size();
    }

    @Override
    public void delete(K key) {
        isClosed();
        map.remove(key);
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        map.put(key, value);
    }

    @Override
    public V read(K key) {
        isClosed();
        return map.get(key);
    }
}
