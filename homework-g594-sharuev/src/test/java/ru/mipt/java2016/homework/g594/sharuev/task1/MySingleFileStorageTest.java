package ru.mipt.java2016.homework.g594.sharuev.task1;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.sharuev.task2.MyKeyValueStorageFactory;
import ru.mipt.java2016.homework.g594.sharuev.task2.ReflectSerializationStrategy;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class MySingleFileStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return (new MyKeyValueStorageFactory()).open(path, new ReflectSerializationStrategy<String>(), new ReflectSerializationStrategy<String>());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildI2DStorage(String path) {
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildStudentsStorage(String path) {
        return null;
    }
}
