package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Morell on 29.10.2016.
 */
public class OPSSTable<K, V> implements KeyValueStorage<K, V> {
    private class FileWorker {
        private File file;
        private File fileCopy;
        private BufferedOutputStream output;
        private BufferedInputStream input;
        private String filePath;

        private FileWorker(String path, String name, String copy) {
            File check = new File(path);
            if (check.isDirectory()) {
                filePath = path;
                file = new File(path, name);
                fileCopy = new File(path, copy);
            } else {
                throw new RuntimeException();
            }

        }
    }

    // Сериализаторы
    private OPSerializator<K> kSerializator;
    private OPSerializator<Long> longSerializtor = new SerLong();
    private OPSerializator<V> valSerializator;
    // Файлы
    private FileWorker data;
    private FileWorker offset;
    private RandomAccessFile randFile;
    // Счетчики для файлов
    private String myStr = "Myfile";
    private final int validStr = 2 * (myStr.length() + 1);
    private long storageLen;
    private long lastWrite;

    private long curLen() throws IOException {
        return randFile.length();
    }

    private int delCount = 0;
    private int allCount = 0;
    private boolean closed;
    private boolean updated = false;
    // Локи
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    private final HashMap<K, Long> index = new HashMap<>();

    public OPSSTable(String path, OPSerializator<K> keySer, OPSerializator<V> valSer) throws IOException {
        kSerializator = keySer;
        valSerializator = valSer;
        data = new FileWorker(path, "data.txt", "copyData.txt");
        offset = new FileWorker(path, "offset.txt", "copyOffset.txt");
        randFile = new RandomAccessFile(data.file, "rw");
        storageLen = curLen();
        lastWrite = storageLen - 1;
        if (data.file.exists() && offset.file.exists()) {
            data.output = new BufferedOutputStream(new FileOutputStream(data.file, true));
            desirialize();
        } else {
            data.file.createNewFile();
            offset.file.createNewFile();
            data.output = new BufferedOutputStream(new FileOutputStream(data.file, true));
            offset.output = new BufferedOutputStream(new FileOutputStream(offset.file, true));
            offset.output.write(0);
            offset.output.flush();
            offset.output.close();
        }
    }

    private void reWrite() throws IOException {
        if (index.size() * 4 < delCount) {
            data.output.close();
            randFile.close();
            data.fileCopy.createNewFile();
            data.input = new BufferedInputStream(new FileInputStream(data.file));
            data.output = new BufferedOutputStream(new FileOutputStream(data.fileCopy));

            storageLen = 0;
            for (int i = 0; i < allCount; i++) {
                K key = kSerializator.read(data.input);
                V value = valSerializator.read(data.input);

                if (index.containsKey(key)) {
                    storageLen += kSerializator.write(data.output, key);
                    index.put(key, storageLen);
                    storageLen += valSerializator.write(data.output, value) + 8;
                }
            }

            data.input.close();
            data.output.flush();
            data.output.close();
            data.fileCopy.renameTo(data.file);
            randFile = new RandomAccessFile(data.fileCopy, "rw");
            data.output = new BufferedOutputStream(new FileOutputStream(data.fileCopy));
        }
    }

    private void serialize() throws IOException {
        synchronized (index) {
            reWrite();
            data.output.close();
            closed = true;
            randFile.close();
            if (updated) {
                offset.file.delete();
                offset.file.createNewFile();
                updated = false;
                offset.output = new BufferedOutputStream(new FileOutputStream(offset.file));
                //validate(offset.output);
                ByteBuffer sizeOffset = ByteBuffer.allocate(4);
                sizeOffset.putInt(index.size());
                offset.output.write(sizeOffset.array());

                for (Map.Entry<K, Long> entry : index.entrySet()) {
                    kSerializator.write(offset.output, entry.getKey());
                    longSerializtor.write(offset.output, entry.getValue());
                }
                offset.output.flush();
                offset.output.close();
            }
        }

    }

    private void desirialize() throws IOException {
        offset.input = new BufferedInputStream(new FileInputStream(offset.file));
        data.input = new BufferedInputStream(new FileInputStream(data.file));
        ByteBuffer size = ByteBuffer.allocate(4);
        offset.input.read(size.array());
        allCount = size.getInt();
        for (int i = 0; i < allCount; i++) {
            K key = kSerializator.read(offset.input);
            Long space = longSerializtor.read(offset.input);
            index.put(key, space);
        }
        offset.input.close();
        data.input.close();
    }

    @Override
    public V read(K key) throws IllegalStateException {
        readLock.lock();
        isClosed();
        try {
            if (index.containsKey(key)) {
                long space = index.get(key);
                if (space > lastWrite) {
                    data.output.flush();
                    lastWrite = storageLen - 1;
                }
                return valSerializator.randRead(randFile, space);
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected ex");
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean exists(K key) throws IllegalStateException {
        isClosed();
        return index.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {

            isClosed();
            storageLen += kSerializator.write(data.output, key) + 4;
            int valOffset = valSerializator.write(data.output, value) + 4;
            index.put(key, storageLen);
            updated = true;
            storageLen += valOffset;
            allCount++;
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected ex");
        } finally {

            writeLock.unlock();
        }
    }

    @Override
    public Iterator readKeys() {
        readLock.lock();
        try {
            isClosed();
        } finally {
            readLock.unlock();
        }

        return index.keySet().iterator();
    }

    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            isClosed();
            index.remove(key);
            delCount++;
            updated = true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int size() {
        return index.size();
    }

    private void isClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Illegal work with closed file");
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            serialize();
            closed = true;
        } else {
            return;
        }
    }
}
