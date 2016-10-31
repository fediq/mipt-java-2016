package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/**
 * Created by liza on 30.10.16.
 */
public class MyKeyValueStorage<Key, Value> implements KeyValueStorage<Key, Value> {
    private Map<Key, Value> elementsDB = new HashMap<>();
    private final MySerializerInterface<Key> keySerializer;
    private final MySerializerInterface<Value> valueSerializer;
    private RandomAccessFile file;
    private File lockFile;
    private String fileName;

    public MyKeyValueStorage(MySerializerInterface<Key> keySerializer,
            MySerializerInterface<Value> valueSerializer, String path, String fileName)
            throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("No such directory");
        }

        String lockPath = path + File.separator + fileName + ".lock";
        lockFile = new File(lockPath);
        if (!lockFile.createNewFile()) {
            throw new IOException("Database is openned");
        }

        elementsDB = new HashMap<>();
        this.fileName = fileName;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        String databasePath = path + File.separator + this.fileName;
        File database = new File(databasePath);

        file = new RandomAccessFile(database, "rw");
        if (!database.createNewFile()) {
            loadData();
        }
    }

    private void loadData() throws IOException {
        file.seek(0);
        elementsDB.clear();
        long fileLength = file.length();
        while (file.getFilePointer() < fileLength) {
            Key key;
            Value value;
            key = keySerializer.read(file);
            value = valueSerializer.read(file);
            if (elementsDB.containsKey(key)) {
                throw new IOException("Duplicate keys");
            } else {
                elementsDB.put(key, value);
            }
        }
    }

    @Override
    public Value read(Key key) {
        return elementsDB.get(key);
    }

    @Override
    public boolean exists(Key key) {
        return elementsDB.containsKey(key);
    }

    @Override
    public void write(Key key, Value value) {
        elementsDB.put(key, value);
    }

    @Override
    public void delete(Key key) {
        elementsDB.remove(key);
    }

    @Override
    public Iterator<Key> readKeys() {
        return elementsDB.keySet().iterator();
    }

    @Override
    public int size() {
        return elementsDB.size();
    }

    @Override
    public void close() throws IOException {
        file.setLength(0);
        file.seek(0);
        for (Map.Entry<Key, Value> entry : elementsDB.entrySet()) {
            keySerializer.write(file, entry.getKey());
            valueSerializer.write(file, entry.getValue());
        }
        elementsDB.clear();
        file.close();
        Files.delete(lockFile.toPath());
    }
}
