package ru.mipt.java2016.homework.g596.egorov.task3;

/**
 * Created by евгений on 30.10.2016.
 */


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * @author Fedor S. Lavrentyev
 * @since 25.10.16
 */

public class MyAdvancedStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new MyAdvancedKeyValueStorage(path,
                new AdvancedSerializerofString(), new AdvancedSerializerofString());
    }
    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new MyAdvancedKeyValueStorage(path,
                new AdvancedSerializerofInteger(), new AdvancedSerializerofDouble());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new MyAdvancedKeyValueStorage(path,
                new AdvancedSerializerofStudentKey(), new AdvancedSerializerofStudent());
    }
}