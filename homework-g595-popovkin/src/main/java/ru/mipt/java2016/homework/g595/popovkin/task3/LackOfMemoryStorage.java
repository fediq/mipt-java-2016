package ru.mipt.java2016.homework.g595.popovkin.task3;
 
import javafx.util.Pair; 
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.popovkin.task2.*;

import java.io.*; 
import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Long.max;

/**
 * Created by Howl on 11.10.2016.
 */

public class LackOfMemoryStorage<K, V> implements KeyValueStorage<K, V> {
    private static final long P = (long) (1E3 + 3);
    private static final long MOD = (long) (1E9 + 7);
    private static final long CACHE_SIZE = (long) (8);
    private static final long MAX_BITES_TO_SKIP = (long) (250);

    private final HashMap<K, Pair<Long, Long>> offsets = new HashMap<>();
    private final HashMap<K, V> cache = new HashMap<>();
    private final String storageName = "main_storage_file";
    private final String mapStorage = "tree";
    private boolean closed = true;
    private String storageDirName;
    private RandomAccessFile workingFile;
    private long maxOffset;

    private ParserInterface<K> keyParser = null;
    private ParserInterface<V> valueParser = null;

    private long getFileHash(String filename) throws IOException {
        long hash = 0;

        FileInputStream in = new FileInputStream(storageDirName + File.separator + filename);
        byte[] buffer = new byte[8 * 1024];
        int newBytes = in.read(buffer);
        while (newBytes > 0) {
            for (int i = 0; i < newBytes; ++i) {
                hash = (hash * P + (long) (buffer[i])) % MOD;
            }
            newBytes = in.read(buffer);
        }
        in.close();
        return hash;
    }

    private boolean testFile(String filename) {
        try {
            InputStream hin = new FileInputStream(storageDirName + File.separator + filename + ".hash");
            IntegerParser parser = new IntegerParser();
            if ((int) getFileHash(filename) != parser.deserialize(hin) || hin.read() != -1) {
                hin.close();
                return false;
            }
            hin.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    private void setHash(String filename) throws IOException {
        long hash = getFileHash(filename);
        FileOutputStream hout = new FileOutputStream(storageDirName + File.separator + filename + ".hash");
        new IntegerParser().serialize((int) hash, hout);
        hout.close();
    }

    public LackOfMemoryStorage(String directoryname, ParserInterface<K> keyParserTmp,
                               ParserInterface<V> valueParserTmp) throws IOException {
        storageDirName = directoryname;
        keyParser = keyParserTmp;
        valueParser = valueParserTmp;
        closed = false;
        maxOffset = 0;
        File file = new File(storageDirName + File.separator + storageName);
        workingFile = new RandomAccessFile(file, "rw");
        if (!testFile(storageName) || !testFile(mapStorage)) {
            return;
        }
        try {
            RandomAccessFile in = new RandomAccessFile(storageDirName + File.separator + mapStorage, "r");
            LongParserRandomAccess longParser = new LongParserRandomAccess();
            long size = longParser.deserialize(in);
            for (int i = 0; i < size; ++i) {
                K key = keyParser.deserialize(in);
                Pair<Long, Long> offsetAndAmount = new Pair<>(longParser.deserialize(in), longParser.deserialize(in));
                offsets.put(key, offsetAndAmount);
                maxOffset = max(maxOffset, offsetAndAmount.getKey() + offsetAndAmount.getValue());
            }
            in.close();
        } catch (IOException ex) {
            offsets.clear();
            maxOffset = 0;
        }
    }

    private void checkForCloseness() {
        if (closed) {
            throw new IllegalStateException("storage has closed");
        }
    }

    private boolean lightCheckForCloseness() {
        return closed;
    }

    @Override
    public V read(K key) {
        synchronized (this) {
            checkForCloseness();
            V ans = cache.get(key);
            if (ans != null) {
                return ans;
            }
            Pair<Long, Long> tmpOffset = offsets.get(key);
            if (tmpOffset == null) {
                return null;
            }
            try {
                intellectSeek(tmpOffset.getKey());
                return valueParser.deserialize(workingFile);
            } catch (IOException ex) {
                return null;
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (this) {
            checkForCloseness();
            if (cache.containsKey(key)) {
                return true;
            }
            return offsets.containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (this) {
            checkForCloseness();
            cache.put(key, value);
            offsets.put(key, new Pair<>(0L, 0L));
            if (cache.size() >= CACHE_SIZE) {
                writeAllForced();
            }
        }
    }

    private void writeAllForced() {
        for (HashMap.Entry<K, V> entry : cache.entrySet()) {
            writeForced(entry.getKey(), entry.getValue());
        }
        cache.clear();
    }

    private void writeForced(K key, V value) {
        checkForCloseness();
        try {
            intellectSeek(maxOffset);
            valueParser.serialize(value, workingFile);
            long newMaxOffset = workingFile.getFilePointer();
            offsets.put(key, new Pair<>(maxOffset, newMaxOffset - maxOffset));
            maxOffset = newMaxOffset;
        } catch (IOException ex) {
            System.out.println("something goes wrong, I can't write, because has poisoned by IOEx");
        }
    }

    @Override
    public void delete(K key) {
        synchronized (this) {
            checkForCloseness();
            cache.remove(key);
            offsets.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (this) {
            checkForCloseness();
            return offsets.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            checkForCloseness();
            return offsets.size();
        }
    }

    private void intellectSeek(long offset) {
        try {
            long tmp = workingFile.getFilePointer();
            if (tmp < offset && offset - tmp < MAX_BITES_TO_SKIP) {
                workingFile.skipBytes((int) (offset - tmp));
            } else if (tmp != offset) {
                workingFile.seek(offset);
            }
        } catch (IOException ex) {
            return;
        }
    }

    public void close() throws IOException {
        synchronized (this) {
            if (lightCheckForCloseness()) {
                return;
            }
            writeAllForced();
            RandomAccessFile out = new RandomAccessFile(storageDirName + File.separator + mapStorage, "rw");
            LongParserRandomAccess longParser = new LongParserRandomAccess();
            longParser.serialize((long) size(), out);
            closed = true;
            try {
                for (HashMap.Entry<K, Pair<Long, Long>> entry : offsets.entrySet()) {
                    keyParser.serialize(entry.getKey(), out);
                    longParser.serialize(entry.getValue().getKey(), out);
                    longParser.serialize(entry.getValue().getValue(), out);
                }
                out.close();
            } catch (Exception ex) {
                out.close();
            }
            workingFile.close();
            setHash(storageName);
            setHash(mapStorage);
        }
    }
}
