package ru.mipt.java2016.homework.g596.pockonechny.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by celidos on 30.10.16.
 */

public class AdvancedKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new AdvancedKeyValueStorage(path, new IntSerialization(),
                new DoubleSerialization());
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new AdvancedKeyValueStorage(path, new StringSerialization(),
                new StringSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new AdvancedKeyValueStorage(path, new StudentKeySerialization(),
                new StudentSerialization());
    }
}
//INT_TO_DOUBLE, STRING_TO_STRING, STUDENTKEY_TO_STUDENT