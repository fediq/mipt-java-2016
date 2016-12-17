package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.*;
import ru.mipt.java2016.homework.g594.kozlov.task3.StudentKeyComparator;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.Comparator;

/**
 * Created by Anatoly on 07.11.2016.
 */
public class KVStorageFactory {

    public static KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KVStorageImpl<String, String>(path, new StringSerializer(),
                new StringSerializer(), new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KVStorageImpl<Integer, Double>(path, new IntegerSerializer(),
                new DoubleSerializer(), new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KVStorageImpl<StudentKey, Student>(path, new StudentKeySerializer(),
                new StudentSerializer(), new StudentKeyComparator());
    }
}
