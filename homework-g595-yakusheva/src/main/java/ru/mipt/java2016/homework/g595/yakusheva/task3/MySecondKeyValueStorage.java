package ru.mipt.java2016.homework.g595.yakusheva.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * Created by Софья on 16.11.2016.
 */
public class MySecondKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final long NOT_WRITED = -1;
    private static final int BUFFER_SIZE = 1024 * 10;
    private static final int BIG_BUFFER_SIZE = BUFFER_SIZE * 10;
    private static final double REBUILD_COEFFICIENT = 2.0;
    private final Map<K, V> map;
    private final Map<K, Long> bigMap;
    private boolean isClosedFlag;
    private MySecondSerializerInterface<K> keySerializer;
    private MySecondSerializerInterface<V> valueSerializer;
    private MySecondSerializerInterface<Long> biasSerializer;
    private RandomAccessFile keyFile;
    private RandomAccessFile valueFile;
    private File keyf;
    private File valf;
    private File hashFile;
    private File lockf;
    private String way;
    private int reallyStored;
    private LoadingCache<K, V> cache;

    public MySecondKeyValueStorage(String path,
                                   MySecondSerializerInterface<K> newKeySerializerArg,
                                   MySecondSerializerInterface<V> newValueSerializerArg,
                                   int cacheSize)
            throws MalformedDataException {

        way = path;
        keySerializer = newKeySerializerArg;
        valueSerializer = newValueSerializerArg;
        biasSerializer = new MyLongSerializer();

        try {
            lockf = new File(Paths.get(path, "storage_lock.db").toString());
            if (!lockf.createNewFile()) {
                throw new RuntimeException("Somebody already opened a storage");
            }

            reallyStored = 0;

            boolean readFromOldFileFlag = true;
            keyf = new File(Paths.get(path, "storage_keys.db").toString());
            valf = new File(Paths.get(path, "storage_values.db").toString());
            if (!keyf.exists()) {
                keyf.createNewFile();
                if (!valf.exists()) {
                    valf.createNewFile();
                    readFromOldFileFlag = false;
                } else {
                    throw new MalformedDataException("Keys file missing, but values file exists");
                }
            } else {
                if (!valf.exists()) {
                    throw new MalformedDataException("Values file missing, but keys file exists");
                }
            }

            map = new HashMap<K, V>();
            bigMap = new HashMap<K, Long>();
            keyFile = new RandomAccessFile(keyf.getPath(), "rw");
            valueFile = new RandomAccessFile(valf.getPath(), "rw");

            if (readFromOldFileFlag) {
                isClosedFlag = false;
                hashFile = new File((Paths.get(way, "storage_hash.db").toString()));
                DataInputStream hashDataInputStream = new DataInputStream(new FileInputStream(hashFile));
                long hash = hashDataInputStream.readLong();
                long readenHash = readFromFile();
                if (hash != readenHash) {
                    throw new MalformedDataException("File was changed");
                }
                hashDataInputStream.close();
            }
        } catch (IOException e) {
            throw new MalformedDataException("error!");
        }

        cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build(
                        new CacheLoader<K, V>() {
                            public V load(K key) throws IOException {
                                valueFile.seek(bigMap.get(key));
                                DataInputStream valuesDataInputStream = new DataInputStream(new BufferedInputStream(
                                        Channels.newInputStream(valueFile.getChannel()), BUFFER_SIZE));
                                return valueSerializer.deserializeFromStream(valuesDataInputStream);
                            }
                        });
    }

    private void closedCheck() {
        if (isClosedFlag) {
            throw new RuntimeException("error!");
        }
    }

    @Override
    public V read(K key) {
        synchronized (this) {
            closedCheck();
            try {
                Long addr = bigMap.get(key);
                V value;
                if (addr == null) {
                    return null;
                } else if (addr == NOT_WRITED) {
                    value = map.get(key);
                } else {
                    value = cache.getUnchecked(key);
                    valueFile.seek(addr);
                }
                return value;
            } catch (IOException e) {
                throw new RuntimeException("error!");
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (this) {
            closedCheck();

            return bigMap.containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (this) {
            closedCheck();
            ++reallyStored;
            map.put(key, value);
            bigMap.put(key, NOT_WRITED);
            if (map.size() > 1023) {
                writePieToFile();
            }
            if (reallyStored > REBUILD_COEFFICIENT * bigMap.size()) {
                try {
                    rebuildStorage();
                } catch (IOException e) {
                    throw new RuntimeException("Can't rebuild storage");
                }
            }
        }
    }

    @Override
    public void delete(K key) {
        synchronized (this) {
            closedCheck();
            bigMap.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (this) {
            closedCheck();
            return bigMap.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            closedCheck();
            return bigMap.size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            if (!isClosedFlag) {
                writePieToFile();
                Adler32 checksum = new Adler32();
                writeKeysToFile();
                valueFile.seek(0);

                valueFile.close();
                keyFile.close();

                // проверяем хеш файла
                byte[] buffer = new byte[BIG_BUFFER_SIZE];
                InputStream valuesDataInputStream = new CheckedInputStream(new BufferedInputStream(
                        new FileInputStream(valf), BIG_BUFFER_SIZE), checksum);
                while (true) {
                    if (valuesDataInputStream.read(buffer) == -1) {
                        break;
                    }
                }
                InputStream keyDataInputStream = new CheckedInputStream(new BufferedInputStream(
                        new FileInputStream(keyf), BIG_BUFFER_SIZE), checksum);
                while (true) {
                    if (keyDataInputStream.read(buffer) == -1) {
                        break;
                    }
                }
                keyDataInputStream.close();
                valuesDataInputStream.close();

                long hash = checksum.getValue();
                hashFile = new File((Paths.get(way, "storage_hash.db").toString()));
                DataOutputStream hashDataOutputStream = new DataOutputStream(new FileOutputStream(hashFile));
                hashDataOutputStream.writeLong(hash);
                hashDataOutputStream.close();

                if (!lockf.delete()) {
                    throw new IOException("Can't delete lock file");
                }
                isClosedFlag = true;
            }
        }
    }

    private long readFromFile() {

        try {
            Adler32 checksum = new Adler32();
            byte[] buffer = new byte[BIG_BUFFER_SIZE];
            InputStream valuesDataInputStream = new CheckedInputStream(new BufferedInputStream(
                    Channels.newInputStream(valueFile.getChannel()), BIG_BUFFER_SIZE), checksum);
            while (true) {
                if (valuesDataInputStream.read(buffer) == -1) {
                    break;
                }
            }
            InputStream keyDataInputStream = new CheckedInputStream(new BufferedInputStream(
                    Channels.newInputStream(keyFile.getChannel()), BIG_BUFFER_SIZE), checksum);
            while (true) {
                if (keyDataInputStream.read(buffer) == -1) {
                    break;
                }
            }

            keyFile.seek(0);
            valueFile.seek(0);

            DataInputStream keysDataInputStream = new DataInputStream(new BufferedInputStream(
                    Channels.newInputStream(keyFile.getChannel()), BIG_BUFFER_SIZE));
            int count = keysDataInputStream.readInt();
            reallyStored = keysDataInputStream.readInt();
            for (int i = 0; i < count; i++) {
                K newKey = keySerializer.deserializeFromStream(keysDataInputStream);
                Long newBias = biasSerializer.deserializeFromStream(keysDataInputStream);
                bigMap.put(newKey, newBias);
            }

            keyFile.setLength(0);

            return checksum.getValue();
        } catch (IOException e) {
            throw new RuntimeException("error!");
        }
    }

    private void writePieToFile() {
        try {
            valueFile.seek(valueFile.length());
            Long initialPosition = valueFile.getFilePointer();
            DataOutputStream valuesDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    Channels.newOutputStream(valueFile.getChannel()), BIG_BUFFER_SIZE));
            for (Map.Entry<K, V> entry : map.entrySet()) {
                K nextKey = entry.getKey();
                bigMap.put(nextKey, initialPosition + (long) valuesDataOutputStream.size());
                valueSerializer.serializeToStream(valuesDataOutputStream, entry.getValue());
            }
            map.clear();
            valuesDataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("error!");
        }
    }

    private void rebuildStorage() throws IOException {
        File newvf = new File(Paths.get(way, "tmp-storage_values.db").toString());
        if (!newvf.exists()) {
            newvf.createNewFile();
        }
        RandomAccessFile newValueFile = new RandomAccessFile(newvf.getPath(), "rw");
        DataOutputStream newValuesDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                Channels.newOutputStream(newValueFile.getChannel()), BIG_BUFFER_SIZE));

        newValueFile.writeInt(bigMap.size());

        Long initialPosition = newValueFile.getFilePointer();
        for (Map.Entry<K, Long> entry : bigMap.entrySet()) {
            valueFile.seek(entry.getValue());
            DataInputStream valuesDataInputStream = new DataInputStream(new BufferedInputStream(
                    Channels.newInputStream(valueFile.getChannel()), BUFFER_SIZE));
            V nextValue = valueSerializer.deserializeFromStream(valuesDataInputStream);

            bigMap.put(entry.getKey(), initialPosition + (long) newValuesDataOutputStream.size());
            valueSerializer.serializeToStream(newValuesDataOutputStream, nextValue);
        }

        newValuesDataOutputStream.flush();
        newValueFile.close();
        valueFile.close();
        if (!valf.delete()) {
            throw new IOException("Can't delete temp value file");
        }
        if (!newvf.renameTo(valf)) {
            throw new IOException("Can't rename temp value file");
        }
        valueFile = new RandomAccessFile(valf, "rw");
    }

    private void writeKeysToFile() throws IOException {
        keyFile.seek(0);
        DataOutputStream keysDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                Channels.newOutputStream(keyFile.getChannel()), BIG_BUFFER_SIZE));
        keyFile.writeInt(bigMap.size());
        keyFile.writeInt(reallyStored);

        for (Map.Entry<K, Long> entry : bigMap.entrySet()) {
            keySerializer.serializeToStream(keysDataOutputStream, entry.getKey());
            biasSerializer.serializeToStream(keysDataOutputStream, entry.getValue());
        }
        keysDataOutputStream.flush();
        keyFile.close();
    }
}
