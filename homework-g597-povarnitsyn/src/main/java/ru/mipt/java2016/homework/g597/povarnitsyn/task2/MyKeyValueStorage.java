package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;


/**
 * Created by Ivan on 30.10.2016.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    public HashMap<K, V> map;
    private SerializationInterface<K> keySerializer;
    private SerializationInterface<V> valueSerializer;
    private File file;
    private static String secretString = "thisFileIsMy.LondonIsTheCapitalOfGreatBritain";


    public MyKeyValueStorage(String path, String name,
                             SerializationInterface<K> keySerializer,
                             SerializationInterface<V> valueSerializer) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        file = new File(path + File.separator + name);
        map = new HashMap<K, V>();

        if(!file.exists()){
            PrintWriter output;
            file.createNewFile();
            output = new PrintWriter(new FileWriter(file));
            output.println(secretString);
            output.println("0");
            output.close();
            return;
        }

        BufferedReader input;
        input = new BufferedReader(new FileReader(file));
        String secret = input.readLine();

        if (!secret.equals(secretString)){
            throw new IOException("It is not my file\n");
        }
        Integer size = Integer.parseInt(input.readLine());

        for (int i = 0; i < size; ++i) {
            K key = keySerializer.deserialize(input);
            V value = valueSerializer.deserialize(input);
            map.put(key, value);
        }
        input.close();
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        PrintWriter output = new PrintWriter(new FileWriter(file));
        output.println(secretString);
        output.println((Integer.valueOf(this.size())).toString());
        for (HashMap.Entry<K, V> entry : map.entrySet()) {
            keySerializer.serialize(output, entry.getKey());
            valueSerializer.serialize(output, entry.getValue());
        }
        map = null;
        output.close();
    }


    private void checkNotClosed() {
        if (map == null) {
            throw new IllegalStateException("Already closed");
        }
    }
}
