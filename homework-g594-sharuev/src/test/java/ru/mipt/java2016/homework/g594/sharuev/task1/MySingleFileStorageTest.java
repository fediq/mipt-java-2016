package ru.mipt.java2016.homework.g594.sharuev.task1;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.sharuev.task2.KeyValueStorageFactory;
import ru.mipt.java2016.homework.g594.sharuev.task2.MyKeyValueStorageFactory;
import ru.mipt.java2016.homework.g594.sharuev.task2.PODSerializer;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class MySingleFileStorageTest extends AbstractSingleFileStorageTest {
    KeyValueStorageFactory factory;

    {
        factory = new MyKeyValueStorageFactory();
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return factory.open(path,
                new PODSerializer<String>(String.class), new PODSerializer<String>(String.class));
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return factory.open(path,
                new PODSerializer<Integer>(Integer.class), new PODSerializer<Double>(Double.class));
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return factory.open(path,
                new PODSerializer<StudentKey>(StudentKey.class),
                new PODSerializer<Student>(Student.class));
    }
}
