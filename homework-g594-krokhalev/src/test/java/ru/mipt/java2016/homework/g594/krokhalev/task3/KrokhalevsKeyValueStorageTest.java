package ru.mipt.java2016.homework.g594.krokhalev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class KrokhalevsKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        SerializationStrategy<String> s = new Serializer<String>(String.class);
        return new KrokhalevsKeyValueStorage<String, String>(path, s, s);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        SerializationStrategy<Integer> s1 = new Serializer<Integer>(Integer.class);
        SerializationStrategy<Double>  s2 = new Serializer<Double>(Double.class);
        return new KrokhalevsKeyValueStorage<Integer, Double>(path, s1, s2);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        SerializationStrategy<StudentKey> s1 = new Serializer<StudentKey>(StudentKey.class);
        SerializationStrategy<Student>  s2 = new Serializer<Student>(Student.class);
        return new KrokhalevsKeyValueStorage<StudentKey, Student>(path, s1, s2);
    }
}
