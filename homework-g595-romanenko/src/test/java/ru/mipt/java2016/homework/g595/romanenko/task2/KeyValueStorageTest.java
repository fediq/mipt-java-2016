package ru.mipt.java2016.homework.g595.romanenko.task2;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 *
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/

public class KeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorage<>(
                path,
                SerializersFactory.StringSerializer.getInstance(),
                SerializersFactory.StringSerializer.getInstance());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildI2DStorage(String path) {
        return new MyKeyValueStorage<>(
                path,
                SerializersFactory.IntegerSerializer.getInstance(),
                SerializersFactory.DoubleSerializer.getInstance());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildStudentsStorage(String path) {
        return new MyKeyValueStorage<>(
                path,
                SerializersFactory.StudentKeySerializer.getInstance(),
                SerializersFactory.StudentSerializer.getInstance());
    }
}
