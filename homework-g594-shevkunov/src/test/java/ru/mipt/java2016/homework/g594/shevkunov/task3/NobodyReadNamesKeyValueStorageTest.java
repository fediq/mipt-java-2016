package ru.mipt.java2016.homework.g594.shevkunov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g594.shevkunov.task2.BinarySerializator;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Tester for task3
 * Created by shevkunov on 14.11.16.
 */
public class NobodyReadNamesKeyValueStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new LazyMergedKeyValueStorage<>(new BinarySerializator<String>("String"),
                    new BinarySerializator<String>("String"), path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new LazyMergedKeyValueStorage<>(new BinarySerializator<Integer>("Integer"),
                    new BinarySerializator<Double>("Double"), path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new LazyMergedKeyValueStorage<>(new BinarySerializator<StudentKey>("StudentKey"),
                    new BinarySerializator<Student>("Student"), path);
        } catch (Exception e) {
            return null;
        }
    }
}
