package ru.mipt.java2016.homework.g594.krokhalev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;

/**
 * Created by wheeltune on 19.10.16.
 */
public class KrokhalevsKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String STORAGE_NAME = "storage.db";

    private File storageFile;

    KrokhalevsKeyValueStorage (String workDirectoryName) {
        super();

        File workDirectory = new File(workDirectoryName);
        storageFile = null;

        if (workDirectory.exists() && workDirectory.isDirectory()) {
            for (File item : workDirectory.listFiles()) {
                if (item.isFile() && item.getName().equals(STORAGE_NAME)) {
                    storageFile = item;
                    break;
                }
            }

            if (storageFile == null) {
                storageFile = new File(workDirectory.getAbsolutePath() + STORAGE_NAME);
                try
                {
                    boolean created = storageFile.createNewFile();
                    if (!created) {
                        //
                    }
                } catch(IOException ex){

                    System.out.println(ex.getMessage());
                }
            }

//            FileOutputStream fos = new FileOutputStream(STORAGE_NAME);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.close();

        } else {
            //
        }
    }

    @Override
    public V read(K key) {
        return null;
    }

    @Override
    public boolean exists(K key) {
        return false;
    }

    @Override
    public void write(K key, V value) {

    }

    @Override
    public void delete(K key) {

    }

    @Override
    public Iterator<K> readKeys() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
