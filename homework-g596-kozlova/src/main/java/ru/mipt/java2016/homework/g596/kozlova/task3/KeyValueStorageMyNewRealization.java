package ru.mipt.java2016.homework.g596.kozlova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

public class KeyValueStorageMyNewRealization<K, V> implements KeyValueStorage<K, V> {
    /*
    *  Данные хранятся следующим образом:
    *
    *   1) Глобальный файл:
    *   Типы хранящихся пар key-value в формате: "<тип key> --- <тип value>"
    *   Количество файлов с данными = k
    *   Количество данных - пар key-value = n
    *   n строк с тройками вида: ключ key; номер файла, содержащего key; отступ в данном файле, соответствующий key
    *
    *   2) Каждый из n файлов с данными(их именя - имя глобального + уникальный номер):
    *   Значения value, соответствующие ключам keys, для которых указаны данный файл и отступ
    *
    *   3) Вспомогательный файл с hash-значениями, соответствующими файлам с данными:
    *   Количество фойлов с данными = k
    *   k строк с числами, сопоставленными каждому файлу
    * */
    private String fileName; // имя файла базы данных (глобальный файл)
    private String hashFileName; // имя hash файла для вспомогательных данных
    private String typeOfData;  // типы хранящихся значений в формате: "<тип 1> --- <тип 2>"
    private HashMap<K, LocationOfKey> mapPlace;  // местонахождения ключей
    private HashMap<K, V> mapData;   // данные - пары (ключ-значение)
    private HashSet<K> setKeys;    // множество всех кючей
    private MySerialization<K> keySerializator;
    private MySerialization<V> valueSerializator;
    private ArrayList<RandomAccessFile> files;  // файлы, в которых кранятся данные
    private Adler32 validate;

    private class LocationOfKey {
        private int fileNumber; // номер файла
        private long shift; // отступ в файле

        LocationOfKey(int fileNumber, long shift) {
            this.fileNumber = fileNumber;
            this.shift = shift;
        }
    }

    public KeyValueStorageMyNewRealization(String path, MySerialization serKey, MySerialization serValue) {
        typeOfData = serKey.getClass() + " --- " + serValue.getClass();
        fileName = path + "/store";
        hashFileName = path + "/hash.txt";
        keySerializator = serKey;
        valueSerializator = serValue;
        mapPlace = new HashMap<>();
        mapData = new HashMap<>();
        setKeys = new HashSet<>();
        files = new ArrayList<>();
        validate = new Adler32();

        String nameCurrentFile = getFileName(-1);
        File file = new File(nameCurrentFile);
        File hashFile = new File(hashFileName);

        if (!file.exists()) {
            createFile(file, hashFile);
        } else {
            getAllData(hashFile, nameCurrentFile);
        }
    }

    private void createFile(File file, File hashFile) { // если файла не существует - создаем его
        try {
            file.createNewFile();
            if (!hashFile.exists()) {
                hashFile.createNewFile();
            }
        } catch (IOException e) {
            throw new MalformedDataException("We can't create file");
        }
    }

    private void getAllData(File hashFile, String nameCurrentFile) { // считываем все ключи
        if (!hashFile.exists()) {
            throw new MalformedDataException("We can't find file");
        }
        try (DataInputStream readFromFile = new DataInputStream(new FileInputStream(nameCurrentFile))) {
            if (!readFromFile.readUTF().equals(typeOfData)) { // наши типы и хранящиеся в базе данных не совпадают
                throw new MalformedDataException("This is invalid file");
            }
            int numberOfFiles = readFromFile.readInt(); // количество файлов с данными
            try (DataInputStream readFromHashFile = new DataInputStream(new FileInputStream(hashFileName))) {
                if (numberOfFiles != readFromHashFile.readInt()) {
                    throw new MalformedDataException("There are invalid data base");
                }
                for (int i = 0; i < numberOfFiles; ++i) {
                    getFileHash(validate, getFileName(i));
                }
                if (numberOfFiles != 0 && validate.getValue() != readFromHashFile.readLong()) {
                    throw new MalformedDataException("There are invalid Data base");
                }
            } catch (IOException e) {
                throw new MalformedDataException("We can't find or read file");
            }

            for (int i = 0; i < numberOfFiles; ++i) {
                File currentFile = new File(getFileName(i));
                if (!currentFile.exists()) {
                    throw new MalformedDataException("We can't find file with data");
                }
                files.add(new RandomAccessFile(currentFile, "rw"));
            }
            int numberOfLines = readFromFile.readInt();
            for (int i = 0; i < numberOfLines; ++i) {
                K key = keySerializator.read(readFromFile.readUTF());
                int fileNumber = readFromFile.readInt();
                long shift = readFromFile.readLong();
                mapPlace.put(key, new LocationOfKey(fileNumber, shift));
                setKeys.add(key);
            }
        } catch (IOException e) {
            throw new MalformedDataException("We can't read from file");
        }
    }

    private static final int MAX_SIZE_OF_DATA = 1300;

    private void getFileHash(Adler32 adler, String name) { // сопоставление каждому файлу его hash-значение
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(name)));
             CheckedInputStream checkedInputStream = new CheckedInputStream(inputStream, adler)) {
            byte[] buffer = new byte[MAX_SIZE_OF_DATA * 100];
            while (checkedInputStream.read(buffer) != -1) {
                continue;
            }
        } catch (FileNotFoundException e) {
            throw new MalformedDataException("We can't find file");
        } catch (IOException e) {
            throw new MalformedDataException("We can't read file");
        }
    }

    private void decreaseData(boolean checkClose) { // уменьшение хранящихся данных - перераспределение по файлам
        if (checkClose || mapData.size() >= MAX_SIZE_OF_DATA) {
            int numberNewFile = files.size();
            String nameNewFile = getFileName(numberNewFile);
            File file = new File(nameNewFile);

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new MalformedDataException("We can't create new file");
                }
            }
            try {
                files.add(new RandomAccessFile(nameNewFile, "rw"));
                RandomAccessFile currentFile = files.get(numberNewFile);
                currentFile.setLength(0);
                currentFile.seek(0);
                for (Map.Entry<K, V> entry: mapData.entrySet()) {
                    if (entry.getValue().equals(null)) {
                        mapPlace.remove(entry.getKey());
                        continue;
                    }
                    LocationOfKey newLocation = new LocationOfKey(numberNewFile, currentFile.getFilePointer());
                    mapPlace.put(entry.getKey(), newLocation);
                    currentFile.writeUTF(valueSerializator.write(entry.getValue()));
                }
                mapData.clear();
                getFileHash(validate, nameNewFile);
            } catch (IOException e) {
                throw new MalformedDataException("We can't get RandomAccessFile");
            }
        }
    }

    /*
    * взятие нужного файла
    * */
    private String getFileName(Integer i) {
        if (i.equals(-1)) {
            return fileName + ".txt";
        }
        return fileName + i.toString() + ".txt";
    }

    /*
    * проверка, что наша директория открыта
    * */
    private void checkOpenedStorage() {
        if (mapData == null) {
            throw new MalformedDataException("Storage already closed");
        }
    }

    /*
    * взятие значения по ключу key
    * */
    @Override
    public V read(K key) {
        checkOpenedStorage();
        if (mapData.keySet().contains(key)) {
            return mapData.get(key);
        }
        if (mapPlace.keySet().contains(key)) {
            int fileNumber = mapPlace.get(key).fileNumber;
            long shift = mapPlace.get(key).shift;
            RandomAccessFile currentFile = files.get(fileNumber);
            try {
                currentFile.seek(shift);
                return valueSerializator.read(currentFile.readUTF());
            } catch (IOException e) {
                throw new MalformedDataException("There aren't necessary data");
            }
        }
        return null;
    }

    /*
    * существование ключа key
    * */
    @Override
    public boolean exists(K key) {
        checkOpenedStorage();
        return (setKeys.contains(key));
    }

    /*
    * добавить пару ключ key - значение value
    * */
    @Override
    public void write(K key, V value) {
        checkOpenedStorage();
        mapData.put(key, value);
        setKeys.add(key);
        decreaseData(false);
    }

    /*
    * удалить ключ key и соответствующее значение value
    * */
    @Override
    public void delete(K key) {
        checkOpenedStorage();
        mapPlace.remove(key);
        setKeys.remove(key);
    }

    /*
    * итератор по ключам
    * */
    @Override
    public Iterator<K> readKeys() {
        checkOpenedStorage();
        return setKeys.iterator();
    }

    /*
    * количество пар ключ - значение
    * */
    @Override
    public int size() {
        checkOpenedStorage();
        return setKeys.size();
    }

    /*
    * закрытие директории
    * */
    @Override
    public void close() throws IOException {
        checkOpenedStorage();
        decreaseData(true);

        // запись всего имеющегося в базу данных
        String currentFile = getFileName(-1);
        try (DataOutputStream writeToFile = new DataOutputStream(new FileOutputStream(currentFile))) {
            writeToFile.writeUTF(typeOfData);
            writeToFile.writeInt(files.size());
            writeToFile.writeInt(mapPlace.size());
            for (Map.Entry<K, LocationOfKey> entry : mapPlace.entrySet()) {
                writeToFile.writeUTF(keySerializator.write(entry.getKey()));
                writeToFile.writeInt(entry.getValue().fileNumber);
                writeToFile.writeLong(entry.getValue().shift);
            }
        }

        // запись вспомогательных данных в hash файл
        try (DataOutputStream writeToHashFile = new DataOutputStream(new FileOutputStream(hashFileName))) {
            writeToHashFile.writeInt(files.size());
            for (int i = 0; i < files.size(); ++i) {
                files.get(i).close();
            }
            writeToHashFile.writeLong(validate.getValue());
        }
        mapData = null;
    }
}