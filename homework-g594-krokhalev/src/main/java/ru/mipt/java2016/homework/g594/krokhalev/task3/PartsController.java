package ru.mipt.java2016.homework.g594.krokhalev.task3;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;

public class PartsController<K, V> implements Closeable {

    private File ssTable;
    private File workDirectory;

    private Class<K> keyClass;
    private Class<V> valueClass;

    private ArrayList<StoragePart<K, V>> parts  = new ArrayList<>();

    private BigInteger nextId = BigInteger.ONE;

    private Map<K, V> memTable = new HashMap<>();
    private Set<K> mKeys = new HashSet<K>();

    private int mVersion = 0;

    private File getFile(int ind) {
        String name = "Part_" + String.valueOf(ind);

        return new File(workDirectory.getAbsolutePath() + File.separator + name);
    }

    private File getTmpFile() {
        String name = "Part_TMP";
        return new File(workDirectory.getAbsolutePath() + File.separator + name);
    }

    public PartsController(File ssTable, Class<K> keyClass, Class<V> valueClass) throws IOException {
        this.ssTable = ssTable;
        this.keyClass = keyClass;
        this.valueClass = valueClass;

        workDirectory = new File(ssTable.getParentFile().getAbsolutePath() + File.separator + "Parts");

        if (!workDirectory.mkdir()) {
            throw new RuntimeException("Bad directory");
        }

        File part = getFile(parts.size());
        if (!ssTable.renameTo(part)) {
            throw new RuntimeException("Bad directory");
        }

        parts.add(new StoragePart<>(part, keyClass, valueClass));
        mKeys.addAll(parts.get(0).getKeys());
    }

    public void flush() throws IOException {
        addPart(new StoragePart<>(memTable, getFile(parts.size()), keyClass, valueClass));
        memTable.clear();
    }

    private void addPart(StoragePart<K, V> newPart) throws IOException {
        while (parts.size() > 0) {
            StoragePart<K, V> last = parts.get(parts.size() - 1);
            if (last.getCapacity() == newPart.getCapacity()) {

                parts.remove(parts.size() - 1);

                if (!last.renameFileTo(getTmpFile())) {
                    throw new RuntimeException();
                }
                StoragePart<K, V> tmpPart = new StoragePart<K, V>(last, newPart, getFile(parts.size()));

                newPart.close();
                last.close();

                newPart = tmpPart;
            } else {
                break;
            }
        }
        parts.add(newPart);
    }

    public V getValue(K key) throws IOException {
        if (memTable.containsKey(key)) {
            return memTable.get(key);
        }
        for (StoragePart<K, V> iPart : parts) {
            if (iPart.containsKey(key)) {
                return iPart.getValue(key);
            }
        }
        return null;
    }

    public boolean isExistKey(K key) {
        return mKeys.contains(key);
    }

    public void setValue(K key, V value) throws IOException {
        if (memTable.size() == KrokhalevsKeyValueStorage.CACHE_SIZE) {
            flush();
        }

        if (!isExistKey(key)) {
            mVersion++;
            mKeys.add(key);
            memTable.put(key, value);
        } else {
            if (memTable.put(key, value) == null) {
                for (StoragePart<K, V> iPart : parts) {
                    iPart.removeKey(key);
                }
            }
        }
    }

    public void deleteKey(K key) throws IOException {
        mVersion++;
        if (mKeys.remove(key)) {
            if (memTable.remove(key) == null) {
                for (StoragePart<K, V> iPart : parts) {
                    iPart.removeKey(key);
                }
            }
        }

    }

    public int getCountKeys() {
        return mKeys.size();
    }

    public Iterator<K> getKeyIterator() {
        return new Iterator<K>() {
            Iterator<K> iterator = mKeys.iterator();
            private int itVersion = mVersion;
            @Override
            public boolean hasNext() {
                if (mVersion != itVersion) {
                    throw new ConcurrentModificationException();
                }
                return iterator.hasNext();
            }

            @Override
            public K next() {
                if (mVersion != itVersion) {
                    throw new ConcurrentModificationException();
                }
                return iterator.next();
            }
        };
    }

    @Override
    public void close() throws IOException {
        flush();

        OutputStream storageStream = new BufferedOutputStream(new FileOutputStream(ssTable));

        for (StoragePart<K, V> iPart : parts) {
            iPart.copyTo(storageStream);
        }

        for (StoragePart<K, V> iPart : parts) {
            iPart.close();
        }
        if (!workDirectory.delete()) {
            throw new IOException("Can not delete directory");
        }

        storageStream.close();
    }
}
