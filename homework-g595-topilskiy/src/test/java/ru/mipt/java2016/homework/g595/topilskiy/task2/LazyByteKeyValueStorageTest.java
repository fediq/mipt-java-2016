package ru.mipt.java2016.homework.g595.topilskiy.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import ru.mipt.java2016.homework.g595.topilskiy.task2.LazyByteKeyValueStorageInfo;

/**
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
public class LazyByteKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        LazyByteKeyValueStorageInfo storageInfoInit =
                new LazyByteKeyValueStorageInfo(path, "String", "String");
        try {
            return new LazyByteKeyValueStorage<>(storageInfoInit);
        } catch (IOException discardException) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        LazyByteKeyValueStorageInfo storageInfoInit =
                new LazyByteKeyValueStorageInfo(path, "Integer", "Double");

        try {
            return new LazyByteKeyValueStorage<>(storageInfoInit);
        } catch (IOException discardException) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        LazyByteKeyValueStorageInfo storageInfoInit =
                new LazyByteKeyValueStorageInfo(path, "StudentKey", "Student");

        try {
            return new LazyByteKeyValueStorage<>(storageInfoInit);
        } catch (IOException discardException) {
            return null;
        }
    }
}
