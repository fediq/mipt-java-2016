package ru.mipt.java2016.homework.g595.iksanov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Эмиль
 */
public class MyStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyStorage<>(path, StrategyForString.getInstance(), StrategyForString.getInstance());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyStorage<>(path, StrategyForInteger.getInstance(), StrategyForDouble.getInstance());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyStorage<>(path, StrategyForStudentKey.getInstance(), StrategyForStudent.getInstance());
    }

}
