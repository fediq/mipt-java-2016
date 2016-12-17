package ru.mipt.java2016.homework.g595.gusarova.task2;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;


/**
 * Created by Дарья on 30.10.2016.
 */
public class KVStorageTests extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KVStorage<String, String> temp;
        try {
            temp = new KVStorage<String, String>(path,
                    new SerializerAndDeserializerForString(),
                    new SerializerAndDeserializerForString());
        }catch (IOException exp) {
            return null;
        }
        return temp;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        KVStorage<Integer, Double> temp;
        try {
            temp = new KVStorage<Integer, Double>(path,
                    new SerializerAndDeserializerForInteger(),
                    new SerializerAndDeserializerForDouble());
        } catch (IOException exp) {
            return null;
        }
        return temp;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        KVStorage<StudentKey, Student> temp;
        try {
            temp = new KVStorage<StudentKey, Student>(path,
                    new SerializerAndDeserializerForStudentKey(),
                    new SerializerAndDeserializerForStudent());
        } catch (IOException exp) {
            return null;
        }
        return temp;
    }
}
