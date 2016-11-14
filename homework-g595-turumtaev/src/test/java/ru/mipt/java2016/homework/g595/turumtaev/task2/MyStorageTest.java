package ru.mipt.java2016.homework.g595.turumtaev.task2;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by galim on 29.10.2016.
 */
public class MyStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) { return new MyStorage<>(path,
            MyStringSerializationStrategy.getInstance(),
            MyStringSerializationStrategy.getInstance()); }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) { return new MyStorage<>(path,
            MyIntegerSerializationStrategy.getInstance(),
            MyDoubleSerializationStrategy.getInstance()); }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) { return new MyStorage<>(path,
            MyStudentKeySerializationStrategy.getInstance(),
            MyStudentSerializationStrategy.getInstance()); }

}
