package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class Part<K, V> implements Closeable {
    private static final double USE_LIMIT = 0.8;
    private final StorageReader<K, V> mStorageReader;
    private final File mFile;

    private final File mStorageFile;
    private final File mStorageTable;

    private Map<K, Long> mKeysPositions = new LinkedHashMap<K, Long>();
    private RandomAccessFile mRAFile;
    private int mActualSize;

    Part(File workDirectory,
         File storageFile,
         File storageTable,
         StorageReader<K, V> storageReader,
         boolean restore) throws IOException {

        mFile = new File(workDirectory.getAbsolutePath() + File.separatorChar + "Part");
        mStorageReader = storageReader;
        mStorageFile = storageFile;
        mStorageTable = storageTable;

        if (restore) {
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
        } else {
            mActualSize = 0;
        }
        mRAFile = new RandomAccessFile(mFile, "rw");
    }

    int getSize() {
        return mKeysPositions.size();
    }

    int getActualSize() {
        return mActualSize;
    }

    double getUse() {
        if (getActualSize() == 0) {
            return 1;
        }
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

    boolean write(K key, V value) throws IOException {
        mRAFile.seek(mRAFile.length());

        Long exists = mKeysPositions.put(key, mRAFile.getFilePointer());
        mStorageReader.writeValue(value, mRAFile);
        return exists != null;
    }

    @Override
    public void close() throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mStorageTable));

        for (Map.Entry<K, Long> iKeyPosition : mKeysPositions.entrySet()) {
            mStorageReader.writeKey(iKeyPosition.getKey(), bos);
            mStorageReader.writeLong(iKeyPosition.getValue(), bos);
        }

        bos.close();

        mRAFile.close();
        if (!mFile.renameTo(mStorageFile)) {
            throw new RuntimeException("Can not rename Part file to close");
        }
    }

    Set<K> getKeys() {
        return mKeysPositions.keySet();
    }

    private V find(K key) throws IOException {
        Long offset = mKeysPositions.get(key);
        V value = null;

        if (offset != null) {
            mRAFile.seek(offset);

            value = mStorageReader.readValue(mRAFile);
        }
        return value;
    }

    private V rebuildAndFind(K key) throws IOException {
        V value = null;
//        Long findOffset = mKeysPositions.get(key);
//
//        File tmpFile = getTmpFile();
//        if (!mFile.renameTo(tmpFile)) {
//            throw new RuntimeException("Can not create Tmp file to rebuild");
//        }
//
//        PositionBufferedInputStream tmpStream = new PositionBufferedInputStream(new FileInputStream(tmpFile));
//        PositionBufferedOutputStream partStream = new PositionBufferedOutputStream(new FileOutputStream(mFile));
//
//        int buffLen;
//        byte[] buff;
//        for (Map.Entry<K, Long> iKeyPosition : mKeysPositions.entrySet()) {
//            mStorageReader.skip(iKeyPosition.getValue() - tmpStream.getPosition(), tmpStream);
//            iKeyPosition.setValue(partStream.getPosition());
//
//            buffLen = mStorageReader.readInt(tmpStream);
//
//            buff = new byte[buffLen];
//            if (tmpStream.getPosition() == findOffset) {
//                mStorageReader.read(buff, tmpStream);
//                ByteArrayInputStream valueStream = new ByteArrayInputStream(buff);
//                value = mStorageReader.readValue(valueStream);
//                valueStream.close();
//            } else {
//                mStorageReader.read(buff, tmpStream);
//            }
//
//            partStream.write(buff);
//        }
//        tmpStream.close();
//        partStream.close();
//
//        if (!tmpFile.delete()) {
//            throw new RuntimeException("Can not delete Tmp file to rebuild");
//        }

        return value;
    }

    private File getTmpFile() {
        String name = mFile.getAbsolutePath() +
                "Tmp";
        return new File(name);
    }
}
