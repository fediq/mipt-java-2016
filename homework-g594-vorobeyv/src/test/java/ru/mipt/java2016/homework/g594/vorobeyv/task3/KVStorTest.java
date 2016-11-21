package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

/**
 * Created by Morell on 19.11.2016.
 */
public class KVStorTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new OPSSTable<String, String>(path, new SerString(), new SerString());
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new OPSSTable<Integer, Double>(path, new SerInteger(), new SerDouble());
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new OPSSTable<StudentKey, Student>(path, new SerStKey(), new SerStVal());
        } catch (IOException ex) {
            return null;
        }
    }
}
