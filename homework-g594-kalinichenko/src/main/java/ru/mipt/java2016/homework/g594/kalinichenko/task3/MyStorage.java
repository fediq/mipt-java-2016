package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

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
        a.write("abaq", "uu9u");
        a.write("abaw", "uu9u");
        a.write("abar", "uu9u");
        a.write("abar", "uu9u");
        a.write("abar", "uu9u");
        a.write("abar", "uu9u");
        a.write("abar", "uu9u");
        a.write("abar", "uu9u");
        a.write("abar", "uu9u");
        a.write("abad", "uu79u");
        a.write("abae", "uu89u");
        System.out.println(a.size());
        System.out.println(a.read("179"));
        System.out.println(a.read("abac"));
        System.out.println(a.read("abad"));
        System.out.println(a.read("abae"));
        //a.delete("abac");
        //a.delete("abad");
        //a.delete("abae");
        //a.delete("abac");
        System.out.println(a.size());
        System.out.println(a.read("179"));
        System.out.println(a.read("abac"));
        System.out.println(a.read("abad"));
        System.out.println(a.read("abae"));
        a.close();
    }

    private MySerializer<Long> offsetSerializer;
    private MySerializer<Integer> lengthSerializer;
    private MySerializer<K> keySerializer;
    private MySerializer<V> valSerializer;
    private HashMap<K, Long> map;
    private HashMap<K, V> updates;
    private HashMap<K, V> readCache;
    ArrayList<K> cacheQueue;
    int beg;
    Long updatesSize;
    private File datafile;
    private File keyfile;
    private RandomAccessFile values;
    private FileOutputStream valuesout;
    private boolean open = false;
    int inFile = 0;
    String filepath;

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
        //RandomAccessFile datain;
        try {
            //System.out.println("KEK3");
            //datain = new RandomAccessFile(keyfile, "r");
            values.seek(0);
            inFile = lengthSerializer.get(values);
        } catch (Exception exc) {
            throw new IllegalStateException("Failed creating file");
        }
        //System.out.println("LOAD");
    }

    public MyStorage(String path, MySerializer keyS, MySerializer valS) {
        filepath = path;
        lengthSerializer = new MyIntSerializer();
        offsetSerializer = new MyLongSerializer();
        keySerializer = keyS;
        valSerializer = valS;
        map = new HashMap();
        updates = new HashMap();
        readCache = new HashMap();
        cacheQueue = new ArrayList();
        beg = 0;
        updatesSize = 0L;
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
            lengthSerializer.put(dataout, 0);
            try {
                out.close();
                dataout.close();
            } catch (Exception exp) {
                throw new IllegalStateException("Invalid work with file");
            }
        } else {
            if (!datafile.exists()) {
                try {
                    datafile.createNewFile();
                } catch (Exception exp) {
                    throw new IllegalStateException("Invalid work with file");
                }
            }
        }

        try {
            values = new RandomAccessFile(datafile, "rw");
            valuesout = new FileOutputStream(datafile, true);
        } catch (Exception exp) {
            throw new IllegalStateException("Invalid work with file");
        }

        open = true;
        load();
    }
    void checkFileSize()
    {
        try {
            if (25 < inFile && map.size() * 2  < inFile)
                {
                File newDataFile = new File(filepath + "/data2.db");
                if (!newDataFile.exists()) {
                    try {
                        newDataFile.createNewFile();
                    } catch (Exception exp) {
                        throw new IllegalStateException("Invalid work with file");
                    }
                }
                valuesout.close();
                valuesout = new FileOutputStream(newDataFile);
                lengthSerializer.put(valuesout, map.size());
                HashSet Offsets = new HashSet(map.values());
                values.seek(0);
                System.out.println(values.length());
                lengthSerializer.get(values);
                long pointer = values.getFilePointer();
                int newSize = 0;
                while(pointer < values.length()) {
                    V val = valSerializer.get(values);
                    if (Offsets.contains(pointer))
                    {
                        valSerializer.put(valuesout, val);
                        newSize++;
                    }
                    pointer = values.getFilePointer();
                }
                values.close();
                datafile.delete();
                boolean success = newDataFile.renameTo(datafile);
                if (!success)
                {
                    throw new IllegalStateException();
                }
                values = new RandomAccessFile(datafile, "rw");
                inFile = newSize;
            }
        } catch (Exception exp) {
            throw new IllegalStateException("Invalid work with file");
        }

    }

    private void writeValues() {
        checkFileSize();
        inFile += updates.size();
        try {
            for (HashMap.Entry<K, V> elem : updates.entrySet()) {
                map.put(elem.getKey(), values.length());
                valSerializer.put(valuesout, elem.getValue());
            }
            updates.clear();
            updatesSize = 0L;
        }
        catch (Exception exp) {
            throw new IllegalStateException("Invalid write");
        }
    }


    private void checkExistence() {
        if (!open) {
            throw new IllegalStateException("Storage closed");
        }
    }

    @Override
    public V read(K key) {
        checkExistence();
        if (!map.containsKey(key)) {
            return null;
        }
        if (updates.containsKey(key)) {
            return updates.get(key);
        }
        if (readCache.containsKey(key))
        {
            return readCache.get(key);
        }
        Long offset = map.get(key);
        try {
            Long length = values.length();
            if (offset >= length) {
                throw new IllegalStateException("Invalid value file");
            }
            values.seek(offset);
            if (readCache.size() > 150)
            {
                if (beg != cacheQueue.size())
                {
                    //readCache.remove(cacheQueue.get(beg)); ///May remove but needn't. ispr
                    //beg++;
                }
            }
            //readCache.put(key, valSerializer.get(values));
            //cacheQueue.add(key);
            return valSerializer.get(values);// readCache.get(key);
        } catch (Exception exp) {
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
        if (updatesSize > 250)
        {
            writeValues();
        }
        updates.put(key, value);
        updatesSize += 1;
        map.put(key, -1L);
    }

    @Override
    public void delete(K key) {
        checkExistence();
        updates.remove(key);
        map.remove(key);
        checkFileSize();
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
            lengthSerializer.put(out, map.size());
            writeValues();
            for (HashMap.Entry<K, Long> elem: map.entrySet()) {
                keySerializer.put(out, elem.getKey());
                offsetSerializer.put(out, elem.getValue());
            }
            out.close();
            values.seek(0);
            ByteBuffer data = ByteBuffer.allocate(Integer.BYTES);
            data.putInt(inFile);
            values.write(data.array());
            values.close();
            valuesout.close();
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }
}
