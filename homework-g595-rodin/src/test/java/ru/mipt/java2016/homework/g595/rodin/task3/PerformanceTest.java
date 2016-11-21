package ru.mipt.java2016.homework.g595.rodin.task3;

import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.CSerializeDouble;
import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.CSerializeInteger;
import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.CSerializeString;
import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.ISerialize;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by dmitry on 21.11.16.
 */
public class PerformanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected COptimizedStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        ISerialize<String> keySerialize = new CSerializeString();
        ISerialize<String> valueSerialize = new CSerializeString();
        return new COptimizedStorage <>(path,keySerialize,valueSerialize);
    }

    @Override
    protected COptimizedStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        ISerialize<Integer> keySerialize = new CSerializeInteger();
        ISerialize<Double> valueSerialize = new CSerializeDouble();
        return new COptimizedStorage<>(path,keySerialize,valueSerialize);
    }

    @Override
    protected COptimizedStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        ISerialize<StudentKey> keySerialize = new CSerializeStudentKey();
        ISerialize<Student> valueSerialize =  new CSerializeStudent();
        return new COptimizedStorage<>(path,keySerialize,valueSerialize);
    }
}
