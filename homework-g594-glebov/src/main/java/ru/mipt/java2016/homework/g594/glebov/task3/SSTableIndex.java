package ru.mipt.java2016.homework.g594.glebov.task3;

import ru.mipt.java2016.homework.g594.glebov.task2.MySerializer;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by daniil on 19.11.16.
 */
public class SSTableIndex<K, V> {
    private HashMap<K, Long> map = new HashMap<>();
    private int mapSize;
    private int deletedElem;
    private String path;
    private MySerializer<K> keySerializer;
    private MySerializer<V> valueSerializer;

    public SSTableIndex(int mapSize, String path, MySerializer<K> keySerializer,
                        MySerializer<V> valueSerializer) throws IOException {
        this.mapSize = mapSize;
        this.path = path;
        long offset = 0;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        deletedElem = 0;
        if (mapSize != 0) {
            try (RandomAccessFile input = new RandomAccessFile(new File(path + File.separator + "storage.db"), "rw")) {
                mapSize = input.readInt();
                for (int i = 0; i < mapSize; i++) {
                    K key = keySerializer.streamDeserialize(input);
                    offset = input.getFilePointer();
                    V value = valueSerializer.streamDeserialize(input);
                    map.put(key, offset);
                }
            } catch (IOException exc) {
                throw new RuntimeException("Can't open file!");
            }
        }
    }

    public Iterator<K> getIterators() {
        return map.keySet().iterator();
    }

    public void insert(K key, long offset) {
        map.put(key, offset);
        mapSize += 1;
    }

    public V returnElem(K key) {
        if (map.containsKey(key)) {
            Long offset = map.get(key);
            try (RandomAccessFile input = new RandomAccessFile(new File(path + File.separator + "storage.db"), "rw")) {
                input.seek(offset);
                V value = valueSerializer.streamDeserialize(input);
                return value;
            } catch (IOException exc) {
                throw new RuntimeException("Can't open working file!");
            }
        } else {
            return null;
        }
    }

    public void delete(K key) {
        map.remove(key);
        mapSize -= 1;
        deletedElem += 1;
    }

    public boolean exists(K key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    public void writeToStorage() {
        File newStor = new File(path + File.separator + "newstorage.db");
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newStor)))) {
            output.writeInt(map.size());
            RandomAccessFile input = new RandomAccessFile(new File(path + File.separator + "storage.db"), "rw");
            for (Map.Entry<K, Long> entry : map.entrySet()) {
                K key = entry.getKey();
                long offset = entry.getValue();
                input.seek(offset);
                V value = valueSerializer.streamDeserialize(input);
                keySerializer.streamSerialize(key, output);
                valueSerializer.streamSerialize(value, output);
            }
            input.close();
            output.close();
            File oldStor = new File(path + File.separator + "storage.db");
            oldStor.delete();
            newStor.renameTo(new File(path + File.separator + "storage.db"));
            if (new File(path + File.separator + "newstorage.db").exists()) {
                File tryNewStor = new File(path + File.separator + "newstorage.db");
                tryNewStor.delete();
            }
        }
        catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

}
