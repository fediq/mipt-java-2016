package ru.mipt.java2016.homework.g594.vishnyakova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Created by Nina on 14.11.16.
 */

/**
 В структуре хранятся все ключи хранилища.
 Для кадого коюча из map можно получить обьект класса Location,
 который дает информацию о номере файла и позиции в нем, где содержится значение соответсвующего ключа.
 При открытии базы из основного файла считываем изформацию для map.
 При добавлении новых элементов, записываем их в tableFresh.
 Как только размер tableFresh превышает MAX_SIZE_OF_FRESH, записываем данные в новый файл на диск.
 Старые значения из файлов не удаляем, просто теряем на них ссылку,
 не записав ее в основной файл в конце работы с базой.
 */

public class UpgKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private class Location {
        private int fileNum;
        private long offset;

        Location(int fileNum, long offset) {
            this.fileNum = fileNum;
            this.offset = offset;
        }
    }

    //Кеш, уменьшает свой размер автоматически когда превышает размер MAX_SIZE_OF_CASH
    private class Cash<K, V> extends LinkedHashMap<K, V> {
        private static final int MAX_SIZE_OF_CASH = 0;

        Cash() {
            super(16, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > MAX_SIZE_OF_CASH;
        }
    }

    private String fileName;
    private String intFileName;
    private String lockFileName;
    private String type;
    private Adler32 validate;
    private NewSerializationStrategy<K> keySerializator;
    private NewSerializationStrategy<V> valSerializator;
    private HashMap<K, Location> mapPlace = new HashMap<K, Location>();
    private Cash<K, V> tableCash = new Cash<K, V>();
    private HashMap<K, V> tableFresh = new HashMap<K, V>();
    private HashSet<K> setExist = new HashSet<K>();
    private boolean opened = true;
    private ArrayList<RandomAccessFile> files = new ArrayList<RandomAccessFile>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    private static final int MAX_SIZE_OF_FRESH = 1300;

    public UpgKeyValueStorage(String typ, String path, NewSerializationStrategy sKey, NewSerializationStrategy sVal) {
        type = typ;
        fileName = path + "/store";
        intFileName = path + "/hash.txt";
        lockFileName = path + "/lock.txt";
        keySerializator = sKey;
        valSerializator = sVal;

        File lockFile = new File(lockFileName);
        writeLock.lock();
        try {
            if (!lockFile.exists()) {
                lockFile.createNewFile();
            } else {
                throw new MalformedDataException("Somebody else is working with data base now");
            }
        } catch (IOException e) {
            throw new MalformedDataException("Couldn't create new file", e);
        } finally {
            writeLock.unlock();
        }

        String mainFile = getFileName(-1);
        File file = new File(mainFile);
        File integrityFile = new File(intFileName);

        writeLock.lock();
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    if (!integrityFile.exists()) {
                        integrityFile.createNewFile();
                    }
                } catch (IOException e) {
                    throw new MalformedDataException("Couldn't create new file", e);
                }
                try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(mainFile));
                     DataOutputStream wrInt = new DataOutputStream(new FileOutputStream(intFileName))) {
                    wr.writeUTF(type);
                    wr.writeInt(0);
                    wr.writeInt(0);
                    wrInt.writeInt(0);
                } catch (IOException e) {
                    throw new MalformedDataException("Couldn't write to file", e);
                }
            }

            if (!integrityFile.exists()) {
                throw new MalformedDataException("Couldn't find file");
            }

            try (DataInputStream rd = new DataInputStream(new FileInputStream(mainFile))) {
                if (!rd.readUTF().equals(type)) {
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
                    K key = keySerializator.deserialize(rd.readUTF());
                    int fileNum = rd.readInt();
                    long offset = rd.readLong();
                    mapPlace.put(key, new Location(fileNum, offset));
                    setExist.add(key);
                }
            } catch (IOException e) {
                throw new MalformedDataException("Couldn't read from file", e);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void getFileHash(Adler32 md, String name) {
        writeLock.lock();
        try (InputStream is = new BufferedInputStream(new FileInputStream(new File(name)));
             CheckedInputStream cis = new CheckedInputStream(is, md)) {
            byte[] buffer = new byte[MAX_SIZE_OF_FRESH * 100];
            while (cis.read(buffer) != -1) {
                continue;
            }
        } catch (FileNotFoundException e) {
            throw new MalformedDataException("Couldn't find file", e);
        } catch (IOException e) {
            throw new MalformedDataException("Couldn't read file", e);
        } finally {
            writeLock.unlock();
        }
    }

    //Проверка, что хеш-сумма по всем файлам базы данных на диске
    // совпадает с сохраненным в отдельном файле значением
    private void checkIntegrity(int numOfFiles) {
        writeLock.lock();
        try (DataInputStream rd = new DataInputStream(new FileInputStream(intFileName))) {
            if (numOfFiles != rd.readInt()) {
                throw new MalformedDataException("Invalid data base");
            }
            validate = new Adler32();
            for (int i = 0; i < numOfFiles; ++i) {
                getFileHash(validate, getFileName(i));
            }
            if (numOfFiles != 0 && validate.getValue() != rd.readLong()) {
                throw new MalformedDataException("Invalid data base");
            }
        } catch (IOException e) {
            throw new MalformedDataException("Couldn't find or read file", e);
        } finally {
            writeLock.unlock();
        }
    }

    private String getFileName(Integer i) {
        if (i.equals(-1)) {
            return fileName + ".txt";
        }
        return fileName + i.toString() + ".txt";
    }

    private void checkIfNotOpened() {
        if (!opened) {
            throw new MalformedDataException("Couldn't work with closed storage");
        }
    }

    // Скидываем на диск в новый файл свежие элементы, когда их стало больше чем MAX_SIZE_OF_FRESH
    private void reduceFresh(boolean doAnyway) {
        writeLock.lock();
        try {
            if (doAnyway || tableFresh.size() >= MAX_SIZE_OF_FRESH) {
                int numNewFile = files.size();
                String newFile = getFileName(numNewFile);
                File file = new File(newFile);

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        throw new MalformedDataException("Couldn't create new file", e);
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
                        curFile.writeUTF(valSerializator.serialize(entry.getValue()));
                    }
                    tableFresh.clear();
                    getFileHash(validate, newFile);
                } catch (IOException e) {
                    throw new MalformedDataException("Couldn't get RandomAccessFile", e);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V read(K key) {
        readLock.lock();
        try {
            checkIfNotOpened();
            if (tableCash.keySet().contains(key)) {
                return tableCash.get(key);
            }
            if (tableFresh.keySet().contains(key)) {
                return tableFresh.get(key);
            }
            if (!mapPlace.keySet().contains(key)) {
                return null;
            }
            int fileNum = mapPlace.get(key).fileNum;
            long offset = mapPlace.get(key).offset;
            RandomAccessFile curFile = files.get(fileNum);
            curFile.seek(offset);
            V value = valSerializator.deserialize(curFile.readUTF());
            tableCash.put(key, value);
            return value;
        } catch (IOException e) {
            throw new MalformedDataException("Couldn't reach needed data in file", e);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        readLock.lock();
        try {
            checkIfNotOpened();
            return (setExist.contains(key));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {
            checkIfNotOpened();
            tableFresh.put(key, value);
            setExist.add(key);
            reduceFresh(false);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            checkIfNotOpened();
            if (exists(key)) {
                mapPlace.remove(key);
                tableCash.remove(key);
                setExist.remove(key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        readLock.lock();
        try {
            checkIfNotOpened();
            return setExist.iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            checkIfNotOpened();
            return setExist.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (!opened) {
            return;
        }
        reduceFresh(true);
        opened = false;
        writeLock.lock();
        try {
            String mainFile = getFileName(-1);
            try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(mainFile))) {
                wr.writeUTF(type);
                wr.writeInt(files.size());
                wr.writeInt(mapPlace.size());
                for (Map.Entry<K, Location> entry : mapPlace.entrySet()) {
                    wr.writeUTF(keySerializator.serialize(entry.getKey()));
                    wr.writeInt(entry.getValue().fileNum);
                    wr.writeLong(entry.getValue().offset);
                }
            }

            try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(intFileName))) {
                wr.writeInt(files.size());
                for (int i = 0; i < files.size(); ++i) {
                    files.get(i).close();
                }
                wr.writeLong(validate.getValue());
            }

            File lockFile = new File(lockFileName);
            lockFile.delete();
        } finally {
            writeLock.unlock();
        }
    }
}