package ru.mipt.java2016.homework.g594.sharuev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

public class OptimizedKvsPerformanceTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(
            String path) throws MalformedDataException {
        return new FsOptimizedKvs<String, String>(path,
                new PojoSerializer<String>(String.class),
                new PojoSerializer<String>(String.class), 0);
        /*return new FsOptimizedKvs<String, String>(path,
                new StringSerializer(), new StringSerializer(), 0);*/
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(
            String path) throws MalformedDataException {
        return new FsOptimizedKvs<Integer, Double>(path,
                new PojoSerializer<Integer>(Integer.class),
                new PojoSerializer<Double>(Double.class), 0);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(
            String path) throws MalformedDataException {
        return new FsOptimizedKvs<StudentKey, Student>(path,
                new PojoSerializer<StudentKey>(StudentKey.class),
                new PojoSerializer<Student>(Student.class), 0);
    }
}
