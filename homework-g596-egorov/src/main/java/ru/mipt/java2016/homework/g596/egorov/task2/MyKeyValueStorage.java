package ru.mipt.java2016.homework.g596.egorov.task2;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g596.egorov.task2.serializers.SerializerInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Автор: Egorov
 * Создано 29.10.16
 */

/**
 * Перзистентное хранилище ключ-значение.
 *
 * Хранилище не обязано сразу же после выполнения запроса изменять состояние на диске, т.е. в процессе работы допустимо
 * расхождение консистентности. Но после выполнения {@link #close()} хранилище должно перейти в консистентное состояние,
 * то есть, на диске должны остаться актуальные данные.
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */

//В данной реализации не используются Input/Outputstream.
//Слишком поздно я о них узнал. Потом переделаю.
//Это даже проще получится...

//Переделал. Прога сократилась в полтора раза.
//Времени ушло меньше в полтора раза.

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final MyFile MyFile;
    private final SerializerInterface<K> keySerializer;
    private final SerializerInterface<V> valueSerializer;
    private final HashMap<K, V> tempStorage = new HashMap<K, V>();
    private static final String CHECKER = "-_- $$It's my directory!$$ -_-";
    private boolean flag = false;


    public MyKeyValueStorage(String dirPath, SerializerInterface<K> keySerializer,
                             SerializerInterface<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        MyFile = new MyFile(dirPath + File.separator + "mydatabase.db"); //File.separator- windows = '\'; unix = '/'


        try {
            if (MyFile.exists()) {
                if (!validateFile()) {
                    throw new RuntimeException("It's an injured/wrong file!");
                }
            }
        } catch (FileNotFoundException e) {   //Если файл был испорчен/ненайден то создаём новый.
            MyFile.createFile();
            MyFile.fill(CHECKER);
        }
    }

    private boolean validateFile() throws FileNotFoundException {
        //раньше, тут был метод, использующий строки.
        //Пришлось его полностью переписать, но возник вопрос:
        //как перевести Java.io.DataInputStream в java.lang.String?

        try (DataInputStream rd = new DataInputStream(new FileInputStream(MyFile.name()))) {
            if (!rd.readUTF().equals(CHECKER)) {
                throw new IllegalStateException("Invalid file");
            }
            int number = rd.readInt();
            for (int i = 0; i < number; ++i) {
                K key = keySerializer.deserialize(rd);
                V val = valueSerializer.deserialize(rd);
                tempStorage.put(key, val);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read from to file");
        }

        return true;
    }

    @Override
    public V read(K key) {
        IsOpen();
        return tempStorage.get(key);
    }

    @Override
    public boolean exists(K key) {
        IsOpen();
        return tempStorage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        IsOpen();
        tempStorage.put(key, value);
    }

    @Override
    public void delete(K key) {
        IsOpen();
        tempStorage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        IsOpen();
        return tempStorage.keySet().iterator();
    }

    @Override
    public int size() {
        IsOpen();
        return tempStorage.size();
    }

    @Override
    public void close() {
        IsOpen();
        try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(MyFile.name()))) {
            wr.writeUTF(CHECKER);
            wr.writeInt(tempStorage.size());
            for (Map.Entry<K, V> entry : tempStorage.entrySet()) {
                keySerializer.serialize(wr, entry.getKey());
                valueSerializer.serialize(wr, entry.getValue());
            }
            flag = false;
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't write storage to file");
        }
    }
    
    private void IsOpen() {
        if (flag) {
            throw new RuntimeException("storage is already closed!");
        }
    }
}


