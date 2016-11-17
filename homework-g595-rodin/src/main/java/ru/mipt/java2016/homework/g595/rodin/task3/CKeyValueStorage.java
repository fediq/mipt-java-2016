package ru.mipt.java2016.homework.g595.rodin.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.CJSONCreator;
import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.ISerialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.util.*;


public class CKeyValueStorage<KeyType, ValueType> implements KeyValueStorage<KeyType, ValueType> {

    private ISerialize<KeyType> keyTypeSerialize;
    private ISerialize<ValueType> valueTypeSerialize;
    private Comparator<KeyType> keyTypeComparator;
    private String dataBaseDirectory;
    private HashMap<KeyType,CKeyInformation> keyMap;
    private CFileHandler lockFileHandler;
    private CFileHandler configurationFile;
    private static final String VALIDATION_STRING = "Task2Storage";


    private LoadingCache<KeyType,ValueType> loadingCache = CacheBuilder.newBuilder()
            .softValues()
            .build(
                    new CacheLoader<KeyType, ValueType>() {
                        @Override
                        public ValueType load(KeyType key) throws CacheException {
                            ValueType result = loadValue(key);
                            if(result == null){
                                throw new CacheException("no such key");
                            }
                            return result;
                        }
                    });

    private Map<String,CFileHandlerWrapper> workingFiles = new TreeMap<>();


    public CKeyValueStorage(String dataBaseDirectory
                            , ISerialize<KeyType> keyTypeSerialize
                            , ISerialize<ValueType> valueTypeSerialize
                            , Comparator<KeyType> keyTypeComparator){

        this.keyTypeComparator = keyTypeComparator;
        this.valueTypeSerialize = valueTypeSerialize;
        this.keyTypeSerialize = keyTypeSerialize;
        this.dataBaseDirectory = buildDirectory(dataBaseDirectory);

        keyMap = new HashMap<>();
        lockFileHandler = new CFileHandler(dataBaseDirectory + "lock");
        synchronized (lockFileHandler){
            checkLock();
        }
        configurationFile = new CFileHandler(dataBaseDirectory + "configuration");


    }

    private void checkLock() {
        if(lockFileHandler.exists()) {
            throw new RuntimeException("Lock file detected");
        }
        lockFileHandler.createFile();
    }



    private boolean validateFile() {
        if(!configurationFile.exists()) {
            return false;
        }
        configurationFile.close();
        String token = configurationFile.readToken();
        if(token == null || !token.equals(VALIDATION_STRING)){
            return false;
        }

        token = configurationFile.readToken();
        if(token == null || !token.equals(keyTypeSerialize.getArgumentClass())) {
            return false;
        }

        token = configurationFile.readToken();
        int number = Integer.getInteger(token);
        for(int i = 0; i < number; ++i) {
            token = configurationFile.readToken();
            CFileHandler fileHandler = new CFileHandler(dataBaseDirectory + token);
            token = configurationFile.readToken();
            long epoch = Long.parseLong(token);
            workingFiles.put(token,new CFileHandlerWrapper(fileHandler,epoch));
        }
        return true;
    }

    private String buildDirectory(String directory){
        if(directory.length() > 0){
            return directory + File.separator;
        } else {
            return "";
        }
    }

    @Override
    public ValueType read(KeyType key) {
        return null;
    }

    @Override
    public boolean exists(KeyType key) {
        return false;
    }

    @Override
    public void write(KeyType key, ValueType value) {

    }

    @Override
    public void delete(KeyType key) {

    }

    @Override
    public Iterator<KeyType> readKeys() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }


    private ValueType loadValue(KeyType key){
        ValueType result = null;
        CKeyInformation information = keyMap.get(key);
        if(information == null){
            return result;
        }
        synchronized (configurationFile){
            CFileHandlerWrapper targetFile = workingFiles.get(information.getFilename());
            KeyType objectKey = keyTypeSerialize.deserialize(targetFile.getFile().readToken());
            if(key.equals(objectKey)){
                result = valueTypeSerialize.deserialize(targetFile.getFile().readToken());
            }
        }
        return result;
    }




}
