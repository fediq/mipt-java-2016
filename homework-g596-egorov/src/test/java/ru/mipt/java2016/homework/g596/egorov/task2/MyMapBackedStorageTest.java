package ru.mipt.java2016.homework.g596.egorov.task2;

/**
 * Created by евгений on 30.10.2016.
 */


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * @author Fedor S. Lavrentyev
 * @since 25.10.16
 */

public class MyMapBackedStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorage<String, String>(path, new SerializerofString(),
                new SerializerofString());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyKeyValueStorage<Integer, Double>(path, new SerializerofInteger(),
                new SerializerofDouble());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyKeyValueStorage<StudentKey, Student>(path, new SerializerofStudentKey(),
                new SerializerofStudent());
    }
}