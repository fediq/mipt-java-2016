package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.g595.tkachenko.task2;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.io.IOException;

public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try (MyKeyValueStorage<String, String> mkvs = new MyKeyValueStorage<>(path, MySerialization.stringStrategy,
                MySerialization.stringStrategy)) {
            return mkvs;
        }
        catch (IOException exc) {
            throw new RuntimeException("Input/output collision!");
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try (MyKeyValueStorage<Integer, Double> mkvs = new MyKeyValueStorage<>(path, MySerialization.intStrategy,
                MySerialization.doubleStrategy)) {
            return mkvs;
        }
        catch (IOException exc) {
            throw new RuntimeException("Input/output collision!");
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try (MyKeyValueStorage<StudentKey, Student> mkvs = new MyKeyValueStorage<>(path, studentKeySerialization,
                studentSerialization)) {
            return mkvs;
        }
        catch (IOException exc) {
            throw new RuntimeException("Input/output collision!");
        }
    }
}
