package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 1 on 30.10.2016.
 */
public class KeyValueStorageImpl implements KeyValueStorage {

    private Boolean fileOpen = true;

    KeyValueStorageImpl(String path, String keyType, String valueType) throws IOException {

        if (keyType == "Integer" && valueType == "Double") {
            serialisator = new IntegerDoubleSerialisator(path);
            mapa = new HashMap<Integer, Double>();
        } else if (keyType == "String" && valueType == "String") {
            serialisator = new StringStringSerialisator(path);
            mapa = new HashMap<String, String>();
        } else if (keyType == "StudentKey" && valueType == "Student") {
            serialisator = new StudentSerialisator(path);
            mapa = new HashMap<StudentKey, Student>();
        }
        if (!serialisator.isGoodFile()) {
            throw new IllegalStateException("Lenin is sad");
        } else {
            serialisator.checkBeforeRead();
            while (true) {
                try {
                    Pair currPair = serialisator.read();
                    mapa.put(currPair.getKey(), currPair.getValue());
                } catch (IOException e) {
                    if (e.getMessage() == "EOF") {
                        break;
                    }
                }
            }
        }
    }

    private ObjectSerialisator serialisator;

    private HashMap mapa;

    @Override
    public Object read(Object key) {
        if (!fileOpen) {
            throw new IllegalStateException("Lenin is sad");
        }
        return mapa.get(key);
    }

    @Override
    public boolean exists(Object key) {
        if (!fileOpen) {
            throw new IllegalStateException("Lenin is sad");
        }
        return mapa.containsKey(key);
    }

    @Override
    public void write(Object key, Object value) {
        if (!fileOpen) {
            throw new IllegalStateException("Lenin is sad");
        }
        mapa.put(key, value);
    }

    @Override
    public void delete(Object key) {
        if (!fileOpen) {
            throw new IllegalStateException("Lenin is sad");
        }
        mapa.remove(key);
    }

    @Override
    public Iterator readKeys() {
        if (!fileOpen) {
            throw new IllegalStateException("Lenin is sad");
        }
        return mapa.keySet().iterator();
    }

    @Override
    public int size() {
        if (!fileOpen) {
            throw new IllegalStateException("Lenin is sad");
        }
        return mapa.size();
    }

    @Override
    public void close() throws IOException {
        serialisator.checkBeforeWrite();

        Iterator currIter = mapa.entrySet().iterator();
        while (currIter.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) currIter.next();
            serialisator.write(thisEntry.getKey(), thisEntry.getValue());
        }
        serialisator.outputStream.close();
        fileOpen = false;
    }
}
