package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 31.10.2016.
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
 * <значение ключа> - n раз.
 *
 *
 * Чтобы достать value по ключу key, нужно сначала вытащить id и shift (смещение относительно начала файла)
 * из storage.db, затем по этому смещению вытащить из storage.db.id значение value.
 *
 * В ходе выполнений моего кода старые данные в старых id-файлах НЕ удаляются (как в той статье про LSM-Tree
 * и было описано), но мы их никогда не сможем прочесть больше.
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
import java.util.HashSet;
import java.util.ArrayList;

public class MyAwesomeUpdatedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String DB_NAME = "storage.db";  // название всей базы в целом
    private static String mainPath; // путь к базе
    private RandomAccessFile fileDB;  // файл-база

    private File ifopen;  // файл для проверки, что database уже открыта
    private ArrayList<File> filesTable; // массив файлов-таблиц (SSTable)

    private HashSet<K> setKeys;  // храним <ключ> в множестве здесь
    private HashMap<K, V> mapKeyValue;  // храним пары <ключ, значение> здесь

    private class PlaceOfKey {
        private int id;
        private long shift;

        PlaceOfKey(int id, long row) {
            this.id = id;
            this.shift = row;
        }

        int getid() {
            return id;
        }

        long getshift() {
            return shift;
        }
    }

    private HashMap<K, PlaceOfKey> mapKeyFile;  // храним данные типа "ключ лежит в id файле" здесь

    private final Serialization<K> keySerialization;
    private final Serialization<V> valueSerialization;

    private static final int MAX_SIZE_OF_DATA = 500;  // максимальное количество ключей в id-файле.


    MyAwesomeUpdatedKeyValueStorage(String path, Serialization<K> keyS, Serialization<V> valueS) throws IOException {

        if (Files.notExists(Paths.get(path))) {
            throw new IOException("Path is wrong.");
        }

        ifopen = new File(path + File.separator + DB_NAME + ".check");
        if (!ifopen.createNewFile()) {
            throw new IOException("DB is already open.");
        }

        setKeys = new HashSet<>();
        mapKeyValue = new HashMap<>();
        mapKeyFile = new HashMap<>();
        filesTable = new ArrayList<>();
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

                setKeys.add(key);
                mapKeyFile.put(key, new PlaceOfKey(id, shift));
            }

            for (int id = 0; id < countFiles; id++) {  // заносим id-файлы в массив
                File currentFile = new File(mainPath + File.separator + DB_NAME + "." + id);
                currentFile.createNewFile();
                filesTable.add(currentFile);
            }
        }
    }

    private void updateData(boolean checkClose) {  // создаем новый id-файл данных
        if (checkClose || mapKeyValue.size() >= MAX_SIZE_OF_DATA) {
            int id = filesTable.size();
            File file = new File(mainPath + File.separator + DB_NAME + "." + id);
            try {
                file.createNewFile();
                filesTable.add(file);

                RandomAccessFile currentFile = new RandomAccessFile(file, "rw");
                currentFile.setLength(0);  // удаляем содержимое файла, если там с номером id уже был
                currentFile.seek(0);

                for (Map.Entry<K, V> entry : mapKeyValue.entrySet()) {  // записываем id-файл
                    PlaceOfKey place = new PlaceOfKey(id, currentFile.getFilePointer());
                    mapKeyFile.put(entry.getKey(), place);
                    valueSerialization.write(currentFile, entry.getValue(), currentFile.getFilePointer());
                }
                mapKeyValue.clear();

                currentFile.close();

            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }


    @Override
    public V read(K key) {
        if (mapKeyValue.keySet().contains(key)) {  // это дает чтение по времени за О(1) для одного и того же ключа.
            return mapKeyValue.get(key);
        } else if (mapKeyFile.containsKey(key)) {  // O(log(n))
            int id = mapKeyFile.get(key).getid();
            long shift = mapKeyFile.get(key).getshift();
            try {
                RandomAccessFile file = new RandomAccessFile(filesTable.get(id), "rw");
                V value = valueSerialization.read(file, shift);
                file.close();
                return value;
            } catch (IOException error) {
                error.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {  // O(log(n))
        return setKeys.contains(key);
    }

    @Override
    public void write(K key, V value) {  // O(log(n))
        setKeys.add(key);
        mapKeyValue.put(key, value);
        updateData(false);
    }

    @Override
    public void delete(K key) {  // O(log(n))
        setKeys.remove(key);
        mapKeyFile.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return setKeys.iterator();
    }

    @Override
    public int size() {
        return setKeys.size();
    }

    @Override
    public void close() throws IOException {
        updateData(true);
        fileDB.setLength(0);  // удаляем содержимое базы
        fileDB.seek(0);

        SerializationInteger intS = new SerializationInteger();
        SerializationLong longS = new SerializationLong();

        intS.write(fileDB, filesTable.size(), fileDB.getFilePointer());
        intS.write(fileDB, mapKeyFile.size(), fileDB.getFilePointer());

        for (Map.Entry<K, PlaceOfKey> entry : mapKeyFile.entrySet()) {  // записываем основную базу
            K key = entry.getKey();
            PlaceOfKey place = entry.getValue();
            keySerialization.write(fileDB, key, fileDB.getFilePointer());
            intS.write(fileDB, place.getid(), fileDB.getFilePointer());
            longS.write(fileDB, place.getshift(), fileDB.getFilePointer());
        }

        fileDB.close();
        Files.delete(ifopen.toPath());
    }

}