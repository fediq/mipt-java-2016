package ru.mipt.java2016.homework.g594.kozlov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.KVStorageImpl;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Anatoly on 07.11.2016.
 */
public class KVStorageFactory {

    public static KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KVStorageImpl<String, String>(path, new StringSerializer(),
                new StringSerializer());
    }

    public static KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KVStorageImpl<Integer, Double>(path, new IntegerSerializer(),
                new DoubleSerializer());
    }

    public static KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KVStorageImpl<StudentKey, Student>(path, new StudentKeySerializer(),
                new StudentSerializer());
    }
}
