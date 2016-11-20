package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by VadimPl on 30.10.16.
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {


    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        SerializationString serializationKey = new SerializationString();
        SerializationString serializationValue = new SerializationString();
        try {
            return new MyKeyValueStorage<String, String>(path, serializationKey, serializationValue);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        SerializationInt serializationKey = new SerializationInt();
        SerializationDouble serializationValue = new SerializationDouble();
        try {
            return new MyKeyValueStorage<Integer, Double>(path, serializationKey, serializationValue);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        SerializationStudentKey serializationKey = new SerializationStudentKey();
        SerializationStudent serializationValue = new SerializationStudent();
        try {
            return new MyKeyValueStorage<StudentKey, Student>(path, serializationKey, serializationValue);
        } catch (IOException e) {
            return null;
        }
    }
}
