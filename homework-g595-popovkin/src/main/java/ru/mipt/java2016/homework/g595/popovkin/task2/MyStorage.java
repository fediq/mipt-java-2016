package ru.mipt.java2016.homework.g595.popovkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by Howl on 11.10.2016.
 */

public class MyStorage<K, V> implements KeyValueStorage<K, V> {
    protected static final long P = (long) (1E3 + 3);
    protected static final long MOD = (long) (1E9 + 7);

    private final HashMap<K, V> storage = new HashMap<>();
    protected boolean closed = true;
    protected String storageDirName;

    private ItemParser<K> keyParser = null;
    private ItemParser<V> valueParser = null;

    protected long getFileHash() throws IOException {
        return 0;
        /*
        FileInputStream in = new FileInputStream(storageDirName + "/main_storage_file");
        //System.out.println("open_getFH");
        long hash = 0;
        byte[] buffer = new byte[1024];
        int newBytes = in.read(buffer);
        while (newBytes > 0) {
            for (int i = 0; i < newBytes; ++i) {
                hash = (hash * P + (long) (buffer[i])) % MOD;
            }
            newBytes = in.read(buffer);
        }
        in.close();
        //System.out.println("close_getFH");
        return hash;
        */
    }

    protected boolean testFile() {
        try {
            InputStream hin = new FileInputStream(storageDirName + "/main_storage_file.hash");
            //System.out.println("open_testF");
            IntegerParser parser = new IntegerParser();
            if ((int) getFileHash() != parser.deserialize(hin) || hin.read() != -1) {
                hin.close();
                //System.out.println("close_testF1");
                //throw new AssertionError("My assert2 bad hash");
                return false;
            }
            hin.close();
            //System.out.println("close_testF2");
        } catch (IOException ex) {
            //System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    protected void setHash() throws IOException {
        long hash = getFileHash();
        FileOutputStream hout = new FileOutputStream(storageDirName + "/main_storage_file.hash");
        //System.out.println("open_setHash");
        new IntegerParser().serialize((int) hash, hout);
        hout.close();
        //System.out.println("close_setHash");
    }

    public MyStorage(String directoryname, ItemParser<K> keyParserTmp,
                     ItemParser<V> valueParserTmp) throws IOException {
        //lock_storage(directoryname, 1);
        //testFileIO();
        storageDirName = directoryname;
        keyParser = keyParserTmp;
        valueParser = valueParserTmp;
        closed = false;
        if (!testFile()) {
            return;
        }
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(storageDirName + "/main_storage_file"));
            //System.out.println("open_construct");
            int size = new IntegerParser().deserialize(in);
            for (int i = 0; i < size; ++i) {
                storage.put(keyParser.deserialize(in), valueParser.deserialize(in));
            }
            in.close();
            //System.out.println("close_construct");
        } catch (IOException ex) {
            storage.clear();
            //System.out.println(ex.getMessage());
        }
    }

    protected void checkForCloseness() {
        if (closed) {
            throw new IllegalStateException("storage has closed");
        }
    }

    @Override
    public V read(K key) {
        checkForCloseness();
        //if (!exists(key)) { return null; }
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkForCloseness();
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkForCloseness();
        storage.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkForCloseness();
        storage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkForCloseness();
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        checkForCloseness();
        return storage.size();
    }

    public void close() throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(storageDirName + "/main_storage_file"));
        //System.out.println("open_close");
        //System.out.println("open_close");
        closed = true;
        new IntegerParser().serialize(storage.size(), out);
        //System.out.println("1");
        try {
            for (HashMap.Entry<K, V> entry : storage.entrySet()) {
                keyParser.serialize(entry.getKey(), out);
                valueParser.serialize(entry.getValue(), out);
            }
        } catch (Exception ex) {
            //System.out.println("forced_close");
            out.close();
        }
        //System.out.println("2");
        out.close();
        //System.out.println("close_close");
        setHash();
        //lock_storage(storageDirName, 0);
    }
}
