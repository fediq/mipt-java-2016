package ru.mipt.java2016.homework.g595.ulyanin.task3;

import org.springframework.security.crypto.codec.Hex;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.ulyanin.task2.IntegerSerializer;
import ru.mipt.java2016.homework.g595.ulyanin.task2.LongSerializer;
import ru.mipt.java2016.homework.g595.ulyanin.task2.Serializer;
import ru.mipt.java2016.homework.g595.ulyanin.task2.StringSerializer;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ulyanin
 * @since 15.11.16
 */
public class SSTable<K, V> implements Closeable {
    private static final String STORAGE_VALIDATE_STRING = "SSTableDB";
    private static final String KEYS_FILE_SUFFIX = "_keys";
    private static final String DATA_FILE_SUFFIX = "_data";
    private static final int MAX_SKIP_BYTES = 300;

    private enum StorageState { OPENED, CLOSED }

    private class ValueInfo {
        private final long valueOffset;

        ValueInfo(long valueOffset) {
            this.valueOffset = valueOffset;
        }
    }

    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private HashMap<K, ValueInfo> storage;
    private RandomAccessFile fileData;
    private RandomAccessFile fileKeys;
    private String baseName;
    private String dbName;
    private String fileNameData;
    private String fileNameKeys;
    private StorageState state;


    public SSTable(String directory, String dbName,
                   Serializer<K> keySerializer,
                   Serializer<V> valueSerializer)
            throws IOException, NoSuchAlgorithmException, ValidationException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.dbName = dbName;
        this.baseName = directory + File.separator + dbName;
        this.fileNameData = this.baseName + DATA_FILE_SUFFIX;
        this.fileNameKeys = this.baseName + KEYS_FILE_SUFFIX;
        storage = new HashMap<>();
        openDatabase();
        state = StorageState.OPENED;
    }

    public String getDBName() {
        return dbName;
    }

    private void readFromFile() throws IOException, ValidationException {
        if (!StringSerializer.getInstance().deserialize(fileKeys).equals(STORAGE_VALIDATE_STRING)) {
            throw new ValidationException("It is not file of dataBase");
        }
        int entriesNumber = IntegerSerializer.getInstance().deserialize(fileKeys);
        for (int i = 0; i < entriesNumber; ++i) {
            K key = keySerializer.deserialize(fileKeys);
            long valueOffset = LongSerializer.getInstance().deserialize(fileKeys);
            storage.put(key, new ValueInfo(valueOffset));
        }
    }

    private RandomAccessFile createNewFile(File file) throws IOException {
        RandomAccessFile newFile;
        if (!file.exists()) {
            if (file.createNewFile()) {
                newFile = new RandomAccessFile(file, "rw");
            } else {
                throw new IOException("cannot create file " + file.getName());
            }
        } else {
            throw new IOException("file already exists");
        }
        return newFile;
    }

    private void openDatabase() throws IOException, ValidationException {
        File fData = new File(fileNameData);
        File fKeys = new File(fileNameKeys);
        if (fKeys.exists() ^ fData.exists()) {
            throw new MalformedDataException("No both keys and data files exist");
        }
        boolean needReading = true;
        if (!fKeys.exists()) {
            fileKeys = createNewFile(fKeys);
            needReading = false;
        }
        if (!fData.exists()) {
            fileData = createNewFile(fData);
        }
        fileKeys = new RandomAccessFile(fKeys, "rw");
        fileData = new RandomAccessFile(fData, "rw");
        if (needReading) {
            readFromFile();
        }
    }


    public int size() {
        throwIfClosed();
        return storage.size();
    }

    public long approximateSizeInBytes() {
        return ObjectSizeFetcher.getObjectSize(storage);
    }

    private String getFileHash(String fileName) throws IOException, NoSuchAlgorithmException {
        /*jumpToBytes(file, 0);
        long h = 0;
        for (int pos = 0; pos != file.length(); ++pos) {
            h = (h * HASH_BASE + file.readUnsignedByte()) % HASH_MODULO;
        }
        return Long.toString(h);*/
        MessageDigest md = MessageDigest.getInstance("MD5");
        String digest = getDigest(new FileInputStream(fileName), md, 2048);
        return digest;
    }

    public static String getDigest(InputStream is, MessageDigest md, int byteArraySize)
            throws NoSuchAlgorithmException, IOException {

        md.reset();
        byte[] bytes = new byte[byteArraySize];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            md.update(bytes, 0, numBytes);
        }
        byte[] digest = md.digest();
        return new String(Hex.encode(digest));
    }

    public String getDataBaseHash() {
        try {
            return getFileHash(fileNameData);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return STORAGE_VALIDATE_STRING;
    }


    private void jumpToBytes(RandomAccessFile file, long position) throws IOException {
        long dist = position - file.getFilePointer();
        if (0 <= dist && dist < MAX_SKIP_BYTES) {
            file.skipBytes((int) dist);
        } else {
            file.seek(position);
        }
    }

    public V read(K key) {
        throwIfClosed();
        if (!storage.containsKey(key)) {
            return null;
        }
        ValueInfo valueInfo = storage.get(key);
        try {
            jumpToBytes(fileData, valueInfo.valueOffset);
            return valueSerializer.deserialize(fileData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean exists(K key) {
        throwIfClosed();
        return storage.containsKey(key);
    }

    public void delete(K key) {
        throwIfClosed();
        storage.remove(key);
    }

    public Iterator<K> readKeys() {
        throwIfClosed();
        return storage.keySet().iterator();
    }


    public void appendEntry(K key, V value) throws IOException {
        throwIfClosed();
        jumpToBytes(fileData, fileData.length());
        storage.put(key, new ValueInfo(fileData.length()));
        valueSerializer.serialize(value, fileData);

    }

    private void throwIfClosed() {
        if (state.equals(StorageState.CLOSED)) {
            throw new IllegalStateException("trying to apply method to closed SSTable");
        }
    }

    @Override
    public void close() throws IOException {
//        fileKeys.setLength(0);
        if (state == StorageState.CLOSED) {
            return;
        }
        fileKeys.seek(0);
        StringSerializer.getInstance().serialize(STORAGE_VALIDATE_STRING, fileKeys);
        IntegerSerializer.getInstance().serialize(storage.size(), fileKeys);
        for (K key : storage.keySet()) {
            keySerializer.serialize(key, fileKeys);
            LongSerializer.getInstance().serialize(storage.get(key).valueOffset, fileKeys);
        }
        fileKeys.close();
        fileData.close();
        state = StorageState.CLOSED;
        storage.clear();
    }
}
