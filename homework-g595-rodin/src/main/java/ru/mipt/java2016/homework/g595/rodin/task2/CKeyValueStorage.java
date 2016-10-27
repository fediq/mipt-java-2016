package ru.mipt.java2016.homework.g595.rodin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.rodin.task2.Serializer.CJSONCreator;
import ru.mipt.java2016.homework.g595.rodin.task2.Serializer.ISerialize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Dmitry on 26.10.16.
 */
public class CKeyValueStorage<KeyType,ValueType> implements KeyValueStorage<KeyType,ValueType> {


    private final CJSONCreator<KeyType,ValueType> serializer;

    private final String keyName;

    private final String valueName;

    private CDiskTable diskWriter;

    private final HashMap<KeyType,ValueType> simpleStorage
            = new HashMap<>();

    private static Boolean closeFlag = false;

    private static final String VALIDATION_STRING = "Task2Storage";

    public CKeyValueStorage(String directoryPath,
                            ISerialize<KeyType> keyTypeSerialize,
                            ISerialize<ValueType> valueTypeSerialize,
                            String keyName,
                            String valueName){
        this.serializer = new CJSONCreator(keyTypeSerialize,valueTypeSerialize);
        this.keyName = keyName;
        this.valueName = valueName;
        diskWriter = new CDiskTable(directoryPath + "/storage.db");
        try {
            if(diskWriter.exists()){
                scanFile();
            }
        } catch (FileNotFoundException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public ValueType read(KeyType key) {
        return simpleStorage.get(key);
    }

    @Override
    public boolean exists(KeyType key) {
        return simpleStorage.containsKey(key);
    }

    @Override
    public void write(KeyType key, ValueType value) {
        simpleStorage.put(key,value);
    }

    @Override
    public void delete(KeyType key) {
        simpleStorage.remove(key);
    }

    @Override
    public Iterator<KeyType> readKeys() {
        return simpleStorage.keySet().iterator();
    }

    @Override
    public int size() {
        return simpleStorage.size();
    }

    @Override
    public void close() throws IOException {
        closeFlag = true;
        flush();
    }

    private void flush() {
        StringBuilder stringBuilder
                = new StringBuilder(VALIDATION_STRING + ";");
        stringBuilder.append(keyName).append(';')
                .append(valueName).append(';')
                .append(String.valueOf(simpleStorage.size()))
                .append(';');
        for(Map.Entry<KeyType,ValueType> entry : simpleStorage.entrySet()){
            stringBuilder
                    .append(serializer.getJSON(entry.getKey(),entry.getValue()))
                    .append(';');
        }
        diskWriter.write(stringBuilder.toString());
    }

    private Boolean scanFile() throws FileNotFoundException
    {
        String fileText;
        try {
            fileText = diskWriter.read();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
        StringTokenizer tokenizer = new StringTokenizer(fileText,";",false);
        String token = tokenizer.nextToken();
        if(!token.equals(VALIDATION_STRING)){
            return false;
        }
        token = tokenizer.nextToken();
        if(!token.equals(keyName)){
            return false;
        }
        token = tokenizer.nextToken();
        if(!token.equals(valueName)){
            return false;
        }
        token = tokenizer.nextToken();
        int size = Integer.parseInt(token);
        simpleStorage.clear();
        for(int i = 0;i < size; ++i){
            token = tokenizer.nextToken();
            try {
                KeyType key = serializer.getKeyFormJSON(token);
                ValueType value = serializer.getValueFromJSON(token);
                simpleStorage.put(key, value);
            } catch (IllegalArgumentException exception){
                return false;
            }
        }
        return true;
    }
}
