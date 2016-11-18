package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by masya on 27.10.16.
 */

class MyStorage<K, V> implements KeyValueStorage<K, V> {

    public static void main(String[] args) {
        MyStringSerializer Str = new MyStringSerializer();
        MyStorage<String, String> a = new MyStorage<String, String>("/home/masya/java", Str, Str);
        a.write("179", "179");
        a.write("abac", "uuu");
        a.write("abad", "u1uu");
        a.write("abae", "7uuu");
        a.write("abac", "uu9u");
        System.out.println(a.size());
        System.out.println(a.read("179"));
        System.out.println(a.read("abac"));
        System.out.println(a.read("abad"));
        System.out.println(a.read("abae"));
        a.delete("abac");
        System.out.println(a.size());
        System.out.println(a.read("179"));
        System.out.println(a.read("abac"));
        System.out.println(a.read("abad"));
        System.out.println(a.read("abae"));
    }
    private MySerializer<Long> offsetSerializer;
    private MySerializer<Integer> lengthSerializer;
    private MySerializer<K> keySerializer;
    private MySerializer<V> valSerializer;
    private HashMap<K, Long> map;
    private File datafile;
    private File keyfile;
    private RandomAccessFile values;
    private FileOutputStream valuesout;
    private boolean open = false;

    private void load() {
        RandomAccessFile in;
        try {
            //System.out.println("KEK3");
            in = new RandomAccessFile(keyfile, "r");
        } catch (Exception exc) {
            throw new IllegalStateException("Failed creating file");
        }
        long len = lengthSerializer.get(in);
        for (int i = 0; i < len; ++i) {
            K key = keySerializer.get(in);
            Long val = offsetSerializer.get(in);
            map.put(key, val);
        }
        byte[] check = new byte[1]; ///check for EOF cause file might be longer
        try {
            if (in.read(check) != -1) {
                throw new IllegalStateException("Invalid file");
            }
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        try {
            in.close();
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        //System.out.println("LOAD");
    }

    MyStorage(String path, MySerializer keyS, MySerializer valS) {
        lengthSerializer = new MyIntSerializer();
        offsetSerializer = new MyLongSerializer();
        keySerializer = keyS;
        valSerializer = valS;
        map = new HashMap();
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalStateException("Wrong path to directory");
        }
        keyfile = new File(path + "/keys.db");
        //System.out.println("KEK");
        datafile = new File(path + "/data.db");
        if (!keyfile.exists()) {
            //System.out.println("KEK2");
            try {
                keyfile.createNewFile();
                //System.out.println("KEK");
            } catch (Exception exp) {

                throw new IllegalStateException("Invalid work with file");
            }
            if (!datafile.exists()) {
                try {
                    datafile.createNewFile();
                } catch (Exception exp) {
                    throw new IllegalStateException("Invalid work with file");
                }
            }
            FileOutputStream out;
            FileOutputStream dataout;
            try {
                out = new FileOutputStream(keyfile);
                dataout = new FileOutputStream(datafile);
            } catch (Exception exp) {
                throw new IllegalStateException("Invalid work with file");
            }
            lengthSerializer.put(out, 0);
            try {
                out.close();
                dataout.close();
            } catch (Exception exp) {
                throw new IllegalStateException("Invalid work with file");
            }
        }
        else
        {
            if (!datafile.exists()) {
                try {
                    datafile.createNewFile();
                } catch (Exception exp) {
                    throw new IllegalStateException("Invalid work with file");
                }
            }
        }

        try {
            values = new RandomAccessFile(datafile, "r");
            valuesout = new FileOutputStream(datafile, true);
        }
        catch (Exception exp) {
            throw new IllegalStateException("Invalid work with file");
        }

        open = true;
        load();
    }

    private void checkExistence() {
        if (!open) {
            throw new IllegalStateException("Storage closed");
        }
    }

    @Override
    public V read(K key) {
        checkExistence();
        if (!map.containsKey(key))
        {
            return null;
        }
        Long offset = map.get(key);
        try
        {
            Long length = values.length();
            if (offset >= length)
            {
                throw new IllegalStateException("Invalid value file");
            }
            values.seek(offset);
            return valSerializer.get(values);
        }
        catch (Exception exp) {
            throw new IllegalStateException("Invalid work with value file");
        }
    }

    @Override
    public boolean exists(K key) {
        checkExistence();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkExistence();
        try
        {
            //System.out.println(values.length());
            map.put(key, values.length());
            valSerializer.put(valuesout, value);
            //System.out.println(values.length());
        }
        catch (Exception exp) {
            throw new IllegalStateException("Invalid work with value file");
        }

    }

    @Override
    public void delete(K key) {
        checkExistence();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkExistence();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() {
        open = false;
        FileOutputStream out;
        try {
            out = new FileOutputStream(keyfile);
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        lengthSerializer.put(out, map.size());
        for (HashMap.Entry<K, Long> elem: map.entrySet()) {
            keySerializer.put(out, elem.getKey());
            offsetSerializer.put(out, elem.getValue());
        }
        try {
            out.close();
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }
}
