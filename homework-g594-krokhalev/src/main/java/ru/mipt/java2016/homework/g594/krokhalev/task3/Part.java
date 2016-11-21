package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class Part<K, V> implements Closeable {
    private static final double USE_LIMIT = 0.8;
    private final StorageReader<K, V> mStorageReader;
    private final File mFile;

    private Map<K, Long> mKeysPositions = new LinkedHashMap<K, Long>();
    private int mActualSize;

    Part(File workDirectory, StorageReader<K, V> storageReader) throws FileNotFoundException {
        mFile = new File(workDirectory.getAbsolutePath() + File.separatorChar + PartsController.getNextPartName());
        mStorageReader = storageReader;
        mActualSize = 0;
    }

    Part(File workDirectory, Map<K, V> memPart, StorageReader<K, V> storageReader) throws IOException {
        mFile = new File(workDirectory.getAbsolutePath() + File.separatorChar + PartsController.getNextPartName());
        mStorageReader = storageReader;
        mActualSize = memPart.size();

        PositionBufferedOutputStream partStream = new PositionBufferedOutputStream(new FileOutputStream(mFile));
        for (Map.Entry<K, V> iPair : memPart.entrySet()) {
            mKeysPositions.put(iPair.getKey(), partStream.getPosition());

            mStorageReader.writeValue(iPair.getValue(), partStream);
        }
        partStream.close();
        memPart.clear();
    }

    Part(File workDirectory,
         File storageFile,
         File storageTable,
         StorageReader<K, V> storageReader) throws IOException {

        mFile = new File(workDirectory.getAbsolutePath() + File.separatorChar + PartsController.getNextPartName());
        mStorageReader = storageReader;

        if (!storageFile.renameTo(mFile)) {
            throw new RuntimeException("Can not create part from storage file");
        }

        BufferedInputStream tableStream = new BufferedInputStream(new FileInputStream(storageTable));
        while (tableStream.available() > 0) {
            K key = mStorageReader.readKey(tableStream);
            Long pos = mStorageReader.readLong(tableStream);

            mKeysPositions.put(key, pos);
        }
        tableStream.close();
        mActualSize = getSize();
    }

    int getSize() {
        return mKeysPositions.size();
    }

    int getActualSize() {
        return mActualSize;
    }

    double getUse() {
        return getSize() / getActualSize();
    }

    boolean exists(K key) {
        return mKeysPositions.containsKey(key);
    }

    boolean remove(K key) {
        return mKeysPositions.remove(key) != null;
    }

    V read(K key) throws IOException {
        V value = null;
        if (getUse() < USE_LIMIT) {
            value = rebuildAndFind(key);
        } else {
            value = find(key);
        }
        return value;
    }

    void append(Part<K, V> appPart) throws IOException {
        PositionBufferedInputStream appStream = new PositionBufferedInputStream(new FileInputStream(appPart.mFile));
        PositionBufferedOutputStream partStream = new PositionBufferedOutputStream(new FileOutputStream(mFile, true));

        mActualSize += appPart.getSize();

        int buffLen;
        byte[] buff;
        long fileSize = mFile.length();
        for (Map.Entry<K, Long> iKeyPosition : appPart.mKeysPositions.entrySet()) {
            mStorageReader.skip(iKeyPosition.getValue() - appStream.getPosition(), appStream);
            iKeyPosition.setValue(partStream.getPosition() + fileSize);

            buffLen = mStorageReader.readInt(appStream);
            buff = new byte[buffLen];
            mStorageReader.read(buff, appStream);

            mStorageReader.writeInt(buffLen, partStream);
            partStream.write(buff);
        }
        appStream.close();
        partStream.close();
        mKeysPositions.putAll(appPart.mKeysPositions);

        appPart.close();
    }

    void save(File storageFile, File storageTable) throws IOException {
        if (getSize() > 0) {
            if (!mFile.renameTo(storageFile)) {
                throw new RuntimeException("Can not rename to save");
            }
            BufferedOutputStream tableStream = new BufferedOutputStream(new FileOutputStream(storageTable));
            for (Map.Entry<K, Long> iKeyPosition : mKeysPositions.entrySet()) {
                mStorageReader.writeKey(iKeyPosition.getKey(), tableStream);
                mStorageReader.writeLong(iKeyPosition.getValue(), tableStream);
            }
            tableStream.close();
        }
    }

    @Override
    public void close() throws IOException {
        if (!mFile.delete()) {
            throw new RuntimeException("Can not delete Part file to close");
        }
    }

    Set<K> getKeys() {
        return mKeysPositions.keySet();
    }

    private V find(K key) throws IOException {
        Long offset = mKeysPositions.get(key);
        V value = null;

        if (offset != null) {
            FileInputStream fileStream = new FileInputStream(mFile);
            mStorageReader.skip(mKeysPositions.get(key), fileStream);

            InputStream partStream = new BufferedInputStream(fileStream);
            value = mStorageReader.readValue(partStream);

            partStream.close();
            fileStream.close();
        }
        return value;
    }

    private V rebuildAndFind(K key) throws IOException {
        V value = null;
        Long findOffset = mKeysPositions.get(key);

        File tmpFile = getTmpFile();
        if (!mFile.renameTo(tmpFile)) {
            throw new RuntimeException("Can not create Tmp file to rebuild");
        }

        PositionBufferedInputStream tmpStream = new PositionBufferedInputStream(new FileInputStream(tmpFile));
        PositionBufferedOutputStream partStream = new PositionBufferedOutputStream(new FileOutputStream(mFile));

        int buffLen;
        byte[] buff;
        for (Map.Entry<K, Long> iKeyPosition : mKeysPositions.entrySet()) {
            mStorageReader.skip(iKeyPosition.getValue() - tmpStream.getPosition(), tmpStream);
            iKeyPosition.setValue(partStream.getPosition());

            buffLen = mStorageReader.readInt(tmpStream);

            buff = new byte[buffLen];
            if (tmpStream.getPosition() == findOffset) {
                mStorageReader.read(buff, tmpStream);
                ByteArrayInputStream valueStream = new ByteArrayInputStream(buff);
                value = mStorageReader.readValue(valueStream);
                valueStream.close();
            } else {
                mStorageReader.read(buff, tmpStream);
            }

            partStream.write(buff);
        }
        tmpStream.close();
        partStream.close();

        if (!tmpFile.delete()) {
            throw new RuntimeException("Can not delete Tmp file to rebuild");
        }

        return value;
    }

    private File getTmpFile() {
        String name = mFile.getAbsolutePath() +
                "Tmp";
        return new File(name);
    }
}
