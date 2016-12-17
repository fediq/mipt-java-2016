package ru.mipt.java2016.homework.g594.ishkhanyan.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g594.ishkhanyan.task2.MyStudentKeySerialization;
import ru.mipt.java2016.homework.g594.ishkhanyan.task2.MyStudentSerialization;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by ${Semien}
 * on ${30.10.16}.
 */
public class MyImprovedKeyValueStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new MyImprovedKeyValueStorage<>(path, "String", "String");
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new MyImprovedKeyValueStorage<>(path, "Integer", "Double");
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new MyImprovedKeyValueStorage<>(path, new MyStudentKeySerialization(), "StudentKey",
                new MyStudentSerialization(), "Student");
    }
}
