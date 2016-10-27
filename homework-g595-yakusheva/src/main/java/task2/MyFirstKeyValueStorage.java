package task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import javax.management.RuntimeErrorException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Софья on 26.10.2016.
 */
public class MyFirstKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final Map<K, V> map;

    public MyFirstKeyValueStorage(String path, MyFirstSerializerInterface<K> newKeySerializerArg,
                                  MyFirstSerializerInterface<V> newValueSerializerArg) {
        File f = new File(Paths.get(path, "storage.db").toString(), "rw");
        keySerializer = newKeySerializerArg;
        valueSerializer = newValueSerializerArg;
        if(!f.exists()){
            try {
                f.createNewFile();
            }
            catch (IOException e){
                throw new RuntimeException( "error: can't create new file") ;
            };
        }

        map = new HashMap<K, V>();
    }

    @Override
    public V read(K key) {
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }

    void readFromFile()
    {
        InputStream inputStream = Channels.newInputStream(file.getChannel());
        K newKey;
        V newValue;
        try {
            count = file.readInt();
        }
        catch (IOException e){
            throw new RuntimeException( "error: can't read from file") ;
        };
        for (int i = 0; i < count; i++){
            try {
                newKey = keySerializer.deserializeFromStream(inputStream);
                newValue = valueSerializer.deserializeFromStream(inputStream);
                map.put(newKey, newValue);
            }
            catch (IOException e){
                throw new RuntimeException( "error: can't read from file") ;
            };
        }
    }

    void writeToFile()
    {   K newKey;
        V newValue;
        OutputStream outputStream = Channels.newOutputStream(file.getChannel());
        try {
            file.writeInt(count);
        }
        catch (IOException e){
            throw new RuntimeException( "error: can't write to file") ;
        };
        for(Map.Entry<K,V> entry : map.entrySet()){
            try {
                keySerializer.serializeToStream(outputStream, entry.getKey());
                valueSerializer.serializeToStream(outputStream, entry.getValue());
            }
            catch (IOException e){
                throw new RuntimeException( "error: can't write to file") ;
            };
        }
    }

    private boolean isClosed;
    private MyFirstSerializerInterface<K> keySerializer;
    private MyFirstSerializerInterface<V> valueSerializer;
    private RandomAccessFile file;
    private int count;
}
