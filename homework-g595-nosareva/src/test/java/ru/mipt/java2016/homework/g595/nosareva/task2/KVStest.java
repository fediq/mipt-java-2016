package ru.mipt.java2016.homework.g595.nosareva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.g595.nosareva.task2.KVStorage;
import ru.mipt.java2016.homework.g595.nosareva.task2.SimpleSerializer;

import java.io.IOException;

/**
 * Created by maria on 26.10.16.
 */
public class KVStest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KVStorage<String, String> res = null;
        try {
            res = new KVStorage<>(path, new SimpleSerializer.SerializerForString(),
                    new SimpleSerializer.SerializerForString());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        }
        return res;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        KVStorage<Integer, Double> res = null;
        try {
            res = new KVStorage<>(path, new SimpleSerializer.SerializerForInteger(),
                    new SimpleSerializer.SerializerForDouble());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        KVStorage<StudentKey, Student> res = null;
        try {
            res = new KVStorage<>(path, new SimpleSerializer.SerializerForStudentKey(),
                    new SimpleSerializer.SerializerForStudent());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        }
        return res;
    }
}
