package ru.mipt.java2016.homework.g596.litvinov.task2;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 31.10.16.
 */
public class MyDBTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyDB<>("storage", path, MySerializationString.getSerialization(),
                    MySerializationString.getSerialization());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyDB<>("storage", path, new MySerializationInteger(),
                    new MySerializationDouble());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyDB<>("storage", path, new MySerializationKeyStudents(),
                    new MySerializationStudent());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
