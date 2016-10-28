package ru.mipt.java2016.homework.g595.topilskiy.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
public class LazyJSONLikeKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return null;
    }
}
