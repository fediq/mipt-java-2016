package ru.mipt.java2016.homework.g594.islamov.task2;

import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KVSImplementation<>(path, new KVStorageStringSerializer(),
                new KVStorageStringSerializer());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KVSImplementation<>(path, new KVStorageIntegerSerializer(),
                new KVStorageDoubleSerializer());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KVSImplementation<>(path, new KVStorageStudentKeySerializer(),
                new KVStorageStudentSerializer());
    }
}
