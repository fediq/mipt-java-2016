package ru.mipt.java2016.homework.g596.egorov.task3;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g596.egorov.task3.serializers.AdvancedSerializerInterface;

import java.io.*;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;


/**
 * Автор: Egorov
 * Создано 29.10.16
 */

/**
 * Перзистентное хранилище ключ-значение.
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */


public class MyAdvancedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private class Location {
        private int fileNum;
        private long offset;

        Location(int fileNum, long offset) {
            this.fileNum = fileNum;
            this.offset = offset;
        }
    }

    private String fileName;
    private String intFileName;
    private static final String CHECKER = "-_- $$It's my directory!$$ -_-";
    private HashMap<K, Location> mapPlace;
    private HashMap<K, V> tableFresh;
    private HashSet<K> keyStorage;
    private AdvancedSerializerInterface<K> keySerializer;
    private AdvancedSerializerInterface<V> valueSerializer;
    private boolean opened;
    private ArrayList<RandomAccessFile> files;
    private Adler32 hashsum;

    private static final int memoryPageSize = 1000;  //размер страницы памяти.
                                                    // Выделяемый буфер под Map кратен размерустраницы памяти

    private void getFileHash(Adler32 md, String name) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(new File(name)));
             CheckedInputStream cheskedStream = new CheckedInputStream(is, md)) {

            byte[] buffer = new byte[memoryPageSize * 100];
            while (cheskedStream.read(buffer) != -1) {
                continue;
            }
        } catch (FileNotFoundException e) {
            throw new MalformedDataException("FILE NOT FIND", e);
        } catch (IOException e) {
            throw new MalformedDataException("READ ERROR", e);
        }
    }


    private void checkIntegrity(int numOfFiles) {
        try (DataInputStream rd = new DataInputStream(new FileInputStream(intFileName))) {

            if (numOfFiles != rd.readInt()) {
                throw new MalformedDataException("INVALID DB");
            }
            hashsum = new Adler32();
            for (int i = 0; i < numOfFiles; ++i) {
                getFileHash(hashsum, getFileName(i));
            }
            if (numOfFiles != 0 && hashsum.getValue() != rd.readLong()) {
                throw new MalformedDataException("INVALID DB");
            }
        } catch (IOException e) {
            throw new MalformedDataException("READ/WRITE ERRORE", e);
        }
    }

    private String getFileName(Integer i) {
        if (i.equals(-1)) {
            return fileName + ".txt";
        }
        return fileName + i.toString() + ".txt";
    }

    private void checkOpenness() {
        if (!opened) {
            throw new MalformedDataException("STORAGE CLOSED");
        }
    }

    private void reduceFresh(boolean close) {
        if (close || tableFresh.size() >= memoryPageSize) {
            int numNewFile = files.size();
            String newFile = getFileName(numNewFile);
            File file = new File(newFile);

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new MalformedDataException("COULDN'T CREATE FILE", e);
                }
            }
            try {
                files.add(new RandomAccessFile(newFile, "rw"));
                RandomAccessFile curFile = files.get(numNewFile);
                curFile.setLength(0);
                curFile.seek(0);
                for (Map.Entry<K, V> entry: tableFresh.entrySet()) {
                    if (entry.getValue().equals(null)) {
                        mapPlace.remove(entry.getKey());
                        continue;
                    }
                    Location newLoc = new Location(numNewFile, curFile.getFilePointer());
                    mapPlace.put(entry.getKey(), newLoc);
                    curFile.writeUTF(valueSerializer.serialize(entry.getValue()));
                }
                tableFresh.clear();
                getFileHash(hashsum, newFile);
            } catch (IOException e) {
                throw new MalformedDataException("COULDN'T GET RANDOMACCESSFILE", e);
            }
        }
    }

    public MyAdvancedKeyValueStorage(String path, AdvancedSerializerInterface sKey, AdvancedSerializerInterface sVal) {
        fileName = path + File.separator + "store";
        intFileName = path + File.separator + "hash.txt";
        keySerializer = sKey;
        valueSerializer = sVal;
        mapPlace = new HashMap<K, Location>();
        tableFresh = new HashMap<K, V>();
        keyStorage = new HashSet<K>();
        opened = true;
        files = new ArrayList<RandomAccessFile>();
        opened = true;

        String mainFile = getFileName(-1);
        File file = new File(mainFile);
        File integrityFile = new File(intFileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
                if (!integrityFile.exists()) {
                    integrityFile.createNewFile();
                }
            } catch (IOException e) {
                throw new MalformedDataException("CREATION ERRORE", e);
            }
            try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(mainFile));
                 DataOutputStream wrInt = new DataOutputStream(new FileOutputStream(intFileName))) {
                wr.writeUTF(CHECKER);
                wr.writeInt(0);
                wr.writeInt(0);
                wrInt.writeInt(0);
            } catch (IOException e) {
                throw new MalformedDataException("WRITING ERRORE", e);
            }
        }

        if (!integrityFile.exists()) {
            throw new MalformedDataException("FILE NOT FOUND");
        }

        try (DataInputStream rd = new DataInputStream(new FileInputStream(mainFile))) {
            if (!rd.readUTF().equals(CHECKER)) {
                throw new MalformedDataException("Invalid file");
            }
            int numberOfFiles = rd.readInt();
            checkIntegrity(numberOfFiles);
            for (int i = 0; i < numberOfFiles; ++i) {
                File curFile = new File(getFileName(i));
                if (!curFile.exists()) {
                    throw new MalformedDataException("Couldn't find file with data");
                }
                files.add(new RandomAccessFile(curFile, "rw"));
            }
            int numberOfLines = rd.readInt();
            for (int i = 0; i < numberOfLines; ++i) {
                K key = keySerializer.deserialize(rd.readUTF());
                int fileNum = rd.readInt();
                long offset = rd.readLong();
                mapPlace.put(key, new Location(fileNum, offset));
                keyStorage.add(key);
            }
        } catch (IOException e) {
            throw new MalformedDataException("Couldn't read from file", e);
        }
    }

    @Override
    public V read(K key) {
        checkOpenness();
        if (tableFresh.keySet().contains(key)) {
            return tableFresh.get(key);
        }
        if (!mapPlace.keySet().contains(key)) {
            return null;
        }
        int fileNum = mapPlace.get(key).fileNum;
        long offset = mapPlace.get(key).offset;
        RandomAccessFile curFile = files.get(fileNum);
        try {
            curFile.seek(offset);
            V value = valueSerializer.deserialize(curFile.readUTF());
            return value;
        } catch (IOException e) {
            throw new MalformedDataException("TOO FEW DATA", e);
        }
    }

    @Override
    public boolean exists(K key) {
        checkOpenness();
        return (keyStorage.contains(key));
    }

    @Override
    public void write(K key, V value) {
        checkOpenness();
        tableFresh.put(key, value);
        keyStorage.add(key);
        reduceFresh(false);
    }

    @Override
    public void delete(K key) {
        checkOpenness();
        if (exists(key)) {
            mapPlace.remove(key);
            keyStorage.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkOpenness();
        return keyStorage.iterator();
    }

    @Override
    public int size() {
        checkOpenness();
        return keyStorage.size();
    }

    @Override
    public void close() throws IOException {
        checkOpenness();
        reduceFresh(true);
        opened = false;

        String mainFile = getFileName(-1);
        try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(mainFile))) {
            wr.writeUTF(CHECKER);
            wr.writeInt(files.size());
            wr.writeInt(mapPlace.size());
            for (Map.Entry<K, Location> entry : mapPlace.entrySet()) {
                wr.writeUTF(keySerializer.serialize(entry.getKey()));
                wr.writeInt(entry.getValue().fileNum);
                wr.writeLong(entry.getValue().offset);
            }
        }

        try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(intFileName))) {
            wr.writeInt(files.size());
            for (int i = 0; i < files.size(); ++i) {
                files.get(i).close();
            }
            wr.writeLong(hashsum.getValue());
        }
    }
}
