package ru.mipt.java2016.homework.g594.kalinichenko.task3;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by masya on 30.10.16.
 */
public class NewMyStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new MyStorage(path, new MyStringSerializer(), new MyStringSerializer());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new MyStorage(path, new MyIntSerializer(), new MyDoubleSerializer());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new MyStorage(path, new MyStudentKeySerializer(), new MyStudentSerializer());
    }
}