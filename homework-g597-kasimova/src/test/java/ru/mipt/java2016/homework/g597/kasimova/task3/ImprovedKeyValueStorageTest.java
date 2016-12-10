package ru.mipt.java2016.homework.g597.kasimova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g597.kasimova.task2.MSerialization;
import ru.mipt.java2016.homework.g597.kasimova.task2.StudentKeySerialization;
import ru.mipt.java2016.homework.g597.kasimova.task2.StudentSerialization;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by Надежда on 23.11.2016.
 */
public class ImprovedKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new ImprovedKeyValueStorage<String, String>(path, MSerialization.STRING_SERIALIZER,
                    MSerialization.STRING_SERIALIZER);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new ImprovedKeyValueStorage<Integer, Double>(path, MSerialization.INTEGER_SERIALIZER,
                    MSerialization.DOUBLE_SERIALIZER);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new ImprovedKeyValueStorage<StudentKey, Student>(path, new StudentKeySerialization(),
                    new StudentSerialization());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
