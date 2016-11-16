package ru.mipt.java2016.homework.g594.kozlov.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.KVStorageFactory;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by Anatoly on 14.11.2016.
 */
public class PerfomanceTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return KVStorageFactory.buildStringsStorage(path);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return KVStorageFactory.buildNumbersStorage(path);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return KVStorageFactory.buildPojoStorage(path);
    }

    /*@Ignore
    @Test
    public void measure100kWDump100kR() {super.measure100kWDump100kR(); }*/
}
