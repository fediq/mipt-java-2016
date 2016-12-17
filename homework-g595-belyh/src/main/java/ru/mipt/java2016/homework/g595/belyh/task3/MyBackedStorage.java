package ru.mipt.java2016.homework.g595.belyh.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import javafx.util.Pair;
import java.nio.channels.FileLock;
import java.io.File;

/**
 * Created by white2302 on 26.11.2016.
 */
public class MyBackedStorage<K, V> implements KeyValueStorage<K, V> {
    private static final Integer SZ = 10;
    private boolean closed;
    private FileLock lock;
    private HashMap<K, Integer> map = new HashMap<>();
    private HashMap<K, V> cache = new HashMap<>();
    private RandomAccessFile file;
    private RandomAccessFile info;
    private RandomAccessFile hash;
    private int base = 1234;
    private int mod = 10007;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private Serializer<Integer> in;
    private String realPath;
    private Integer maxSize;
    private RandomAccessFile lockFile;
    private String nameBase;
    private String nameBuf;

    public MyBackedStorage(String path, Serializer<K> serializerK, Serializer<V> serializerV) throws IOException {
        realPath = path;
        keySerializer = serializerK;
        valueSerializer = serializerV;
        info = new RandomAccessFile(path + File.separator + "DataBase", "rw");
        nameBase = "DataBase";
        nameBuf = "buffer";
        file = new RandomAccessFile(path + File.separator + "StorageInfo", "rw");
        hash = new RandomAccessFile(path + File.separator + "Hash", "rw");
        in = new MySerializer.IntegerSerializer();

        try {
            lockFile = new RandomAccessFile(path + File.separator + "lock", "rw");
            lock = lockFile.getChannel().lock();
        } catch (IOException error) {
            System.out.println("Error");
        }

        int size = 0;

        if (file.length() != 0) {
            int h = in.deserialize(hash);

            if (h != getHash()) {
                throw new IOException("BAD HASH");
            }

            size = in.deserialize(file);
        }

        for (Integer i = 0; i < size; i++) {
            K key = keySerializer.deserialize(file);
            Integer shift = in.deserialize(file);

            map.put(key, shift);
        }

        maxSize = map.size();

        closed = false;
        hash.close();
        file.close();
    }

    private int getHash() throws IOException {
        int h = 0;

        int pos = 0;

        mySeek(file, (long) 0);

        while (pos != file.length()) {
            h *= base;
            h += file.readByte();
            pos++;
            h %= mod;
        }

        mySeek(file, (long) 0);

        return h;
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
        if (closed) {
            return;
        }
        closed = true;

        file = new RandomAccessFile(realPath + File.separator + "StorageInfo", "rw");
        hash = new RandomAccessFile(realPath + File.separator + "Hash", "rw");

        in.serialize(map.size(), file);

        for (HashMap.Entry<K, Integer> it : map.entrySet()) {
            keySerializer.serialize(it.getKey(), file);
            in.serialize(it.getValue(), file);
        }

        mySeek(hash, (long) 0);

        in.serialize(getHash(), hash);

        lock.release();

        hash.close();
        file.close();
        info.close();
        lockFile.close();
    }

    @Override
    public int size() {
        isClosed();
        return map.size();
    }

    private void rebuild() {
        ArrayList<Pair<Integer, K>> list = new ArrayList<>();
        for (HashMap.Entry<K, Integer> it : map.entrySet()) {
            list.add(new Pair<>(it.getValue(), it.getKey()));
        }

        RandomAccessFile buf;

        try {
            buf = new RandomAccessFile(realPath + File.separator + nameBuf, "rw");

            list.sort(new Comparator<Pair<Integer, K>>() {
                public int compare(Pair<Integer, K> x, Pair<Integer, K> y) {
                    return y.getKey() - x.getKey();
                }
            });

            map.clear();

            Integer pos = 0;

            for (int i = 0; i < list.size(); i++) {
                map.put(list.get(i).getValue(), pos);
                mySeek(info, (long) list.get(i).getKey());

                try {
                    V value = valueSerializer.deserialize(buf);
                    mySeek(buf, (long) pos);
                    valueSerializer.serialize(value, buf);
                } catch (IOException error) {
                    System.out.println("Error");
                }
            }

            info.close();
            info = buf;
            File f = new File(realPath + File.separator + nameBase, "rw");
            File g = new File(realPath + File.separator + nameBuf, "rw");
            f.renameTo(g);
        } catch (IOException err) {
            System.out.println("Buffer is not correct");
        }
    }

    @Override
    public void delete(K key) {
        isClosed();

        if (map.containsKey(key)) {
            cache.remove(key);
            map.remove(key);

            if (map.size() <= maxSize / 3) {
                rebuild();
                maxSize = map.size();
            }
        }
    }

    private void addToCache(K key, V value) {
        if (cache.size() == SZ) {
            cache.clear();
        }

        cache.put(key, value);
    }

    private void mySeek(RandomAccessFile f, Long pos) {
        try {
            if (f.getFilePointer() == pos) {
                return;
            }
            f.seek(pos);
        } catch (IOException error) {
            System.out.println("Error");
        }
    }

    @Override
    public void write(K key, V value) {
        isClosed();

        addToCache(key, value);

        try {
            mySeek(info, info.length());
            map.put(key, (int) info.length());
            valueSerializer.serialize(value, info);
            if (map.size() > maxSize) {
                maxSize++;
            }
        } catch (IOException error) {
            System.out.println("Error");
        }
    }

    @Override
    public V read(K key) {
        isClosed();

        if (!exists(key)) {
            return null;
        }

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Integer shift = map.get(key);

        try {
            mySeek(info, (long) shift);
            V tmp = valueSerializer.deserialize(info);
            addToCache(key, tmp);
            return tmp;
        } catch (IOException error) {
            System.out.println("Error");
        }

        return null;
    }
}
