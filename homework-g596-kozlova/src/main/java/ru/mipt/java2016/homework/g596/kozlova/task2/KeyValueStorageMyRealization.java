package ru.mipt.java2016.homework.g596.kozlova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Iterator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class KeyValueStorageMyRealization<K, V> implements KeyValueStorage<K, V> {
    /*
    * Пусть в нашей базе данных все хранится в следующем формате:
    * <Тип ключей> --- <тип значений> // в строковом формате
    * Кол-во данных, т.е. всех таких пар ключ - соответствующее значение
    * Сами данные
    * */

    private String file_name; // путь, по которому находится наша база данных
    private String type_of_data; // типы хранящихся ключей и значений в базе данных
    private HashMap<K, V> map = new HashMap<K, V>();// данные из нашей базы
    private MySerialization<K> key_serializator;
    private MySerialization<V> value_serializator;
    File file;

    public KeyValueStorageMyRealization(String type, String path, MySerialization ser_key, MySerialization ser_val) {
        type_of_data = type;
        file_name = path + "/store.txt";
        key_serializator = ser_key;
        value_serializator = ser_val;
        file = new File(file_name);

        if (!file.exists()) {
            create_file();
        }
        get_all_data();
    }

    private void create_file() {
        // если файла не существует - создаем его
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("We can't create file");
        }
        try (DataOutputStream write_to_file = new DataOutputStream(new FileOutputStream(file_name))) {
            //сразу записываем в файл используемые типы и количество пар данных ключ - значение, т.е. 0
            write_to_file.writeUTF(type_of_data);
            write_to_file.writeInt(0);
        } catch (IOException e) {
            throw new IllegalStateException("We can't write to file");
        }
    }

    private void get_all_data() {
        // считываем все пары ключ - значение из файла
        try (DataInputStream read_from_file = new DataInputStream(new FileInputStream(file_name))) {
            if (!read_from_file.readUTF().equals(type_of_data)) {
                // наши типы и хранящиеся в базе данных не совпадают
                throw new IllegalStateException("This is invalid file");
            }
            int number = read_from_file.readInt(); // кол-во данных
            for (int i = 0; i < number; ++i) {
                K key = key_serializator.read(read_from_file);
                V val = value_serializator.read(read_from_file);
                map.put(key, val);
            }
        } catch (IOException e) {
            throw new IllegalStateException("We can't read from this file");
        }
    }

    /*
    * проверка, что наша директория открыта
    * */
    private void checkOpenedStorage() {
        if (map == null) {
            throw new IllegalStateException("Storage already closed");
        }
    }

    /*
    * взятие значения по ключу key
    * */
    @Override
    public V read(K key) {
        checkOpenedStorage();
        return map.get(key);
    }

    /*
    * существование ключа key
    * */
    @Override
    public boolean exists(K key) {
        checkOpenedStorage();
        return map.containsKey(key);
    }

    /*
    * добавить пару ключ key - значение value
    * */
    @Override
    public void write(K key, V value) {
        checkOpenedStorage();
        map.put(key, value);
    }

    /*
    * удалить ключ key и соответствующее значение value
    * */
    @Override
    public void delete(K key) {
        checkOpenedStorage();
        map.remove(key);
    }

    /*
    * итератор по ключам
    * */
    @Override
    public Iterator<K> readKeys() {
        checkOpenedStorage();
        return map.keySet().iterator();
    }

    /*
    * количество пар ключ - значение
    * */
    @Override
    public int size() {
        checkOpenedStorage();
        return map.size();
    }

    /*
    * закрытие директории
    * */
    @Override
    public void close() throws IOException {
        checkOpenedStorage();
        // запись всего имеющегося в базу данных
        try (DataOutputStream write_to_file = new DataOutputStream(new FileOutputStream(file_name))) {
            write_to_file.writeUTF(type_of_data);
            write_to_file.writeInt(map.size());
            for (HashMap.Entry<K, V> entry: map.entrySet()) {
                key_serializator.write(write_to_file, entry.getKey());
                value_serializator.write(write_to_file, entry.getValue());
            }
            map = null;
        } catch (IOException e) {
            throw new IllegalStateException("We can't write to file");
        }
    }
}
