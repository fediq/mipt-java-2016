package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 * Теперь с жуткой и страшной реализацией!
 *
 * Реализация быстрого хранилища: через улучшенные (под позицию файла) сериализаторы
 * и идей SSTables/LSM-Tree (как я их понял по крайней мере).
 *
 * Суть идей:
 * Есть два типа файла: 1) основной storage.db; 2) сами хранилища storage.db.id (далее именуемые как "id-файлы").
 *
 * 1) storage.db хранит первыми байтами следующие вещи:
 * <количество хранилищ storage.db.1, storage.db.2, ...> = k;
 * <количество ключей> = n;
 * <key, id файла, shift файла> - n раз.
 *
 * 2) storage.db.id, где id - некоторое число, хранит байты записанных значений ключей:
 * <key, value> - n раз.
 *
 *
 *
 * Чтобы достать value по ключу key, нужно сначала вытащить id и shift (смещение относительно начала файла)
 * из storage.db, затем по этому смещению вытащить из storage.db.id значение value.
 *
 * Старые данные удаляются с помощью объединения и уплотнения id-файлов.
 *
 *
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class MyAwesomeUpdatedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String DB_NAME = "storage.db";  // название всей базы в целом
    private static final int MAX_SIZE_OF_KEYS = 500;  // максимальное количество данных в id-файле.

    private class PlaceOfValue {
        private int id;
        private long shift;

        PlaceOfValue(int id, long shift) {
            this.id = id;
            this.shift = shift;
        }

        int getId() {
            return id;
        }

        long getShift() {
            return shift;
        }
    }

    private final String mainPath; // путь к базе
    private final RandomAccessFile fileDB;  // файл-база
    private final File lockfile;  // файл для проверки, что database уже открыта
    private boolean isLockFileExist = false;  // флаг для проверки, что database уже открыта

    private final List<File> filesTable = new ArrayList<>(); // массив файлов-таблиц (SSTable)
    private final Set<K> keySet = new HashSet<>();  // храним <ключ> в множестве здесь
    private final Map<K, V> mapKeyValueCache = new HashMap<>();  // храним пары <ключ, значение> здесь
    private final Map<K, PlaceOfValue> mapKeyFile = new HashMap<>();  // храним данные типа "ключ лежит в id файле"
    private final Map<K, PlaceOfValue> deletedMapKeyFile = new HashMap<>();  // храним удаленные данные здесь

    private final Serialization<K> keySerialization;
    private final Serialization<V> valueSerialization;


    MyAwesomeUpdatedKeyValueStorage(String path, Serialization<K> keyS, Serialization<V> valueS) throws IOException {

        if (Files.notExists(Paths.get(path))) {
            throw new IOException("Path is wrong.");
        }

        lockfile = new File(path + File.separator + DB_NAME + ".check");
        if (!lockfile.createNewFile()) {
            throw new IOException("DB is already open.");
        }
        isLockFileExist = true;

        keySerialization = keyS;
        valueSerialization = valueS;
        mainPath = path;

        File database = new File(mainPath + File.separator + DB_NAME);
        boolean isDBcreated = database.createNewFile();
        fileDB = new RandomAccessFile(database, "rw");

        if (!isDBcreated) {
            SerializationInteger intS = new SerializationInteger();
            SerializationLong longS = new SerializationLong();
            int countFiles = intS.read(fileDB, fileDB.getFilePointer());
            int sizeKeys = intS.read(fileDB, fileDB.getFilePointer());
            for (int i = 0; i < sizeKeys; i++) {  // читаем основную базу
                K key = keySerialization.read(fileDB, fileDB.getFilePointer());
                int id = intS.read(fileDB, fileDB.getFilePointer());
                long shift = longS.read(fileDB, fileDB.getFilePointer());

                keySet.add(key);
                mapKeyFile.put(key, new PlaceOfValue(id, shift));
            }

            for (int id = 0; id < countFiles; id++) {  // заносим id-файлы в массив
                File currentFile = new File(mainPath + File.separator + DB_NAME + "." + id);
                currentFile.createNewFile();
                filesTable.add(currentFile);
            }
        }
    }

    private synchronized void isFileDBopened() throws IllegalStateException {
        if (!isLockFileExist) {  // на случай если какой-то хитрец закроет базу до завершения всех потоков
            throw new IllegalStateException("The storage is closed.");
        }
    }

    private synchronized void mergeIdFiles(int id1, int id2) throws IOException {
        int newid1 = Math.min(id1, id2);  // новый id первого смерженного файла
        int newid2 = Math.max(id1, id2);  // новый id второго смерженного файла (на случай, если первый переполнен)

        HashMap<K, PlaceOfValue> newMapKeyFile = new HashMap<>();  // для обновления исходного mapKeyFile

        File idFile1 = filesTable.get(newid1);
        File idFile2 = filesTable.get(newid2);

        RandomAccessFile idFileRAM1 = new RandomAccessFile(idFile1, "rw");
        RandomAccessFile idFileRAM2 = new RandomAccessFile(idFile2, "rw");
        int currentKeys = 0; // количество записанных ключей в новом файле; их, очевидно, не больше 2*MAX_SIZE_OF_KEYS.
        long currentShift = 0;

        File currentFile = new File(mainPath + File.separator + DB_NAME + "." + "new1");
        currentFile.createNewFile();
        RandomAccessFile currentFileRAM = new RandomAccessFile(currentFile, "rw");

        while (currentShift != idFileRAM1.length()) {  // читаем первый id-файл
            K key = keySerialization.read(idFileRAM1, idFileRAM1.getFilePointer());
            long currentKeyShift = idFileRAM1.getFilePointer();
            V value = valueSerialization.read(idFileRAM1, idFileRAM1.getFilePointer());
            currentShift = idFileRAM1.getFilePointer();

            if (deletedMapKeyFile.containsKey(key)) {
                PlaceOfValue place = deletedMapKeyFile.get(key);
                if (place.getId() == newid1 && place.getShift() == currentKeyShift) {
                    continue;
                }
            }
            keySerialization.write(currentFileRAM, key, currentFileRAM.getFilePointer());
            newMapKeyFile.put(key, new PlaceOfValue(newid1, currentFileRAM.getFilePointer()));
            valueSerialization.write(currentFileRAM, value, currentFileRAM.getFilePointer());
            currentKeys++;
        }

        currentShift = 0;

        while (currentShift != idFileRAM2.length() && currentKeys < MAX_SIZE_OF_KEYS) {  // читаем второй id-файл
            K key = keySerialization.read(idFileRAM2, idFileRAM2.getFilePointer());
            long currentKeyShift = idFileRAM2.getFilePointer();
            V value = valueSerialization.read(idFileRAM2, idFileRAM2.getFilePointer());
            currentShift = idFileRAM2.getFilePointer();
            if (deletedMapKeyFile.containsKey(key)) {
                PlaceOfValue place = deletedMapKeyFile.get(key);
                if (place.getId() == newid2 && place.getShift() == currentKeyShift) {
                    continue;
                }
            }
            keySerialization.write(currentFileRAM, key, currentFileRAM.getFilePointer());
            newMapKeyFile.put(key, new PlaceOfValue(newid2, currentFileRAM.getFilePointer()));
            valueSerialization.write(currentFileRAM, value, currentFileRAM.getFilePointer());
            currentKeys++;
        }

        idFileRAM1.close();
        Files.delete(filesTable.get(id1).toPath());
        currentFileRAM.close();
        currentFile.renameTo(idFile1);

        if (currentKeys >= MAX_SIZE_OF_KEYS) {  // если первый смерженный файл переполнен.
            currentFile = new File(mainPath + File.separator + DB_NAME + "." + "new2");
            currentFile.createNewFile();
            currentFileRAM = new RandomAccessFile(currentFile, "rw");

            while (currentShift != idFileRAM2.length()) {
                K key = keySerialization.read(idFileRAM2, idFileRAM2.getFilePointer());
                long currentKeyShift = idFileRAM2.getFilePointer();
                V value = valueSerialization.read(idFileRAM2, idFileRAM2.getFilePointer());
                currentShift = idFileRAM2.getFilePointer();
                if (deletedMapKeyFile.containsKey(key)) {
                    PlaceOfValue place = deletedMapKeyFile.get(key);
                    if (place.getId() == newid2 && place.getShift() == currentKeyShift) {
                        continue;
                    }
                }
                keySerialization.write(currentFileRAM, key, currentFileRAM.getFilePointer());
                newMapKeyFile.put(key, new PlaceOfValue(newid2, currentFileRAM.getFilePointer()));
                valueSerialization.write(currentFileRAM, value, currentFileRAM.getFilePointer());
            }
        }

        idFileRAM2.close();
        Files.delete(filesTable.get(id2).toPath());
        if (currentKeys >= MAX_SIZE_OF_KEYS) {
            currentFileRAM.close();
            currentFile.renameTo(idFile2);
        } else {  // в случае непереполнения остается один лишний файл, поэтому дальше
            for (int id = newid2 + 1; id < filesTable.size(); id++) {  // меняем названия всех нужных id-файлов
                int oldid = id;
                int newid = id - 1;
                File oldfile = filesTable.get(oldid);

                for (Map.Entry<K, PlaceOfValue> entry : newMapKeyFile.entrySet()) {  // обновляем новые ключи
                    if (entry.getValue().getId() == oldid) {
                        entry.setValue(new PlaceOfValue(newid, entry.getValue().getShift()));
                    }
                }

                File newfile = new File(mainPath + File.separator + DB_NAME + "." + newid);
                oldfile.renameTo(newfile);
            }
        }

        for (Map.Entry<K, PlaceOfValue> entry : newMapKeyFile.entrySet()) {  // обновляем сами исходные ключи
            mapKeyFile.remove(entry.getKey());
            mapKeyFile.put(entry.getKey(), entry.getValue());
        }

    }

    private synchronized void compactIdFiles() {
        try {
            for (int id = 0; id < filesTable.size() - 1; id++) {
                mergeIdFiles(id, id + 1);
            }
        } catch (IOException error) {
            throw new RuntimeException("Something got wrong", error);
        }

    }

    private synchronized void updateData(boolean checkClose) {  // создаем новый id-файл данных
        if (checkClose) {
            isFileDBopened();
        }
        if ((checkClose && deletedMapKeyFile.size() > 0) || deletedMapKeyFile.size() >= 2 * MAX_SIZE_OF_KEYS) {
            compactIdFiles();
            deletedMapKeyFile.clear();
        }
        if ((checkClose && mapKeyValueCache.size() > 0) || mapKeyValueCache.size() >= MAX_SIZE_OF_KEYS) {
            int id = filesTable.size();
            File file = new File(mainPath + File.separator + DB_NAME + "." + id);
            try {
                file.createNewFile();
                filesTable.add(file);

                RandomAccessFile currentFile = new RandomAccessFile(file, "rw");
                currentFile.setLength(0);  // удаляем содержимое файла, если там с номером id уже был
                currentFile.seek(0);

                for (Map.Entry<K, V> entry : mapKeyValueCache.entrySet()) {  // записываем id-файл
                    keySerialization.write(currentFile, entry.getKey(), currentFile.getFilePointer());
                    PlaceOfValue place = new PlaceOfValue(id, currentFile.getFilePointer());
                    mapKeyFile.remove(entry.getKey());
                    mapKeyFile.put(entry.getKey(), place);
                    valueSerialization.write(currentFile, entry.getValue(), currentFile.getFilePointer());
                }
                mapKeyValueCache.clear();

                currentFile.close();

            } catch (IOException error) {
                throw new RuntimeException("Something got wrong", error);
            }
        }
    }


    @Override
    public synchronized V read(K key) {
        isFileDBopened();
        if (mapKeyValueCache.keySet().contains(key)) {  // дает чтение по времени за О(1) для одного и того же ключа.
            return mapKeyValueCache.get(key);
        } else if (mapKeyFile.containsKey(key)) {  // O(log(n))
            int id = mapKeyFile.get(key).getId();
            long shift = mapKeyFile.get(key).getShift();
            try {
                RandomAccessFile file = new RandomAccessFile(filesTable.get(id), "rw");
                V value = valueSerialization.read(file, shift);
                file.close();
                return value;
            } catch (IOException error) {
                throw new RuntimeException("Something got wrong", error);
            }
        } else {
            return null;
        }
    }

    @Override
    public synchronized boolean exists(K key) {  // O(log(n))
        isFileDBopened();
        return keySet.contains(key);
    }

    @Override
    public synchronized void write(K key, V value) {  // O(log(n))
        isFileDBopened();
        if (mapKeyValueCache.containsKey(key) || mapKeyFile.containsKey(key)) {
            PlaceOfValue place = mapKeyFile.get(key);
            deletedMapKeyFile.put(key, place);
        }
        keySet.add(key);
        mapKeyValueCache.put(key, value);
        updateData(false);
    }

    @Override
    public synchronized void delete(K key) {  // O(log(n))
        isFileDBopened();
        if (mapKeyValueCache.containsKey(key) || mapKeyFile.containsKey(key)) {
            PlaceOfValue place = mapKeyFile.get(key);
            deletedMapKeyFile.put(key, place);
        }
        keySet.remove(key);
        mapKeyFile.remove(key);
        mapKeyValueCache.remove(key);
        updateData(false);
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        isFileDBopened();
        return keySet.iterator();
    }

    @Override
    public synchronized int size() {
        isFileDBopened();
        return keySet.size();
    }

    @Override
    public synchronized void close() throws IOException {
        if (!isLockFileExist) {
            return;
        }
        try {
            updateData(true);
            fileDB.setLength(0);  // удаляем содержимое базы
            fileDB.seek(0);

            SerializationInteger intS = new SerializationInteger();
            SerializationLong longS = new SerializationLong();

            intS.write(fileDB, filesTable.size(), fileDB.getFilePointer());
            intS.write(fileDB, mapKeyFile.size(), fileDB.getFilePointer());

            for (Map.Entry<K, PlaceOfValue> entry : mapKeyFile.entrySet()) {  // записываем основную базу
                K key = entry.getKey();
                PlaceOfValue place = entry.getValue();
                keySerialization.write(fileDB, key, fileDB.getFilePointer());
                intS.write(fileDB, place.getId(), fileDB.getFilePointer());
                longS.write(fileDB, place.getShift(), fileDB.getFilePointer());
            }
        } finally {
            fileDB.close();
            Files.delete(lockfile.toPath());
            isLockFileExist = false;
        }
    }

}