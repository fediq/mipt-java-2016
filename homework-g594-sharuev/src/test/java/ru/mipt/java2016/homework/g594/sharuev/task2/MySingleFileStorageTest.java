package ru.mipt.java2016.homework.g594.sharuev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.sharuev.task2.KeyValueStorageFactory;
import ru.mipt.java2016.homework.g594.sharuev.task2.MyKeyValueStorageFactory;
import ru.mipt.java2016.homework.g594.sharuev.task2.POJOSerializer;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class MySingleFileStorageTest extends AbstractSingleFileStorageTest {
    private KeyValueStorageFactory factory;

    {
        factory = new MyKeyValueStorageFactory();
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return factory.open(path,
                new POJOSerializer<String>(String.class), new POJOSerializer<String>(String.class));
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return factory.open(path,
                new POJOSerializer<Integer>(Integer.class), new POJOSerializer<Double>(Double.class));
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return factory.open(path,
                new POJOSerializer<StudentKey>(StudentKey.class),
                new POJOSerializer<Student>(Student.class));
    }
}
