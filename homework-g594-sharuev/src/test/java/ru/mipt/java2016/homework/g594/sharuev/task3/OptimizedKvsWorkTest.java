package ru.mipt.java2016.homework.g594.sharuev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.sharuev.task3.OptimizedKvsFactory;
import ru.mipt.java2016.homework.g594.sharuev.task3.POJOComparator;
import ru.mipt.java2016.homework.g594.sharuev.task3.POJOSerializer;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.Comparator;

public class OptimizedKvsWorkTest extends AbstractSingleFileStorageTest {
    private OptimizedKvsFactory factory;

    {
        factory = new OptimizedKvsFactory();
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return factory.open(path,
                new POJOSerializer<String>(String.class), new POJOSerializer<String>(String.class), new POJOComparator<>(String.class));
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return factory.open(path,
                new POJOSerializer<Integer>(Integer.class),
                new POJOSerializer<Double>(Double.class),
                new POJOComparator<>(Integer.class));
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return factory.open(path,
                new POJOSerializer<StudentKey>(StudentKey.class),
                new POJOSerializer<Student>(Student.class),
                new POJOComparator<>(StudentKey.class));
    }
}
