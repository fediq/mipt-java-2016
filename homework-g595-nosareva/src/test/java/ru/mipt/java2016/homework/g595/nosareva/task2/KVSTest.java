package ru.mipt.java2016.homework.g595.nosareva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by maria on 26.10.16.
 */
public class KVSTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KVStorage<String, String> res = null;
        try {
            res = new KVStorage<>(path, new SerializerForString(),
                    new SerializerForString());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        }
        return res;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        KVStorage<Integer, Double> res = null;
        try {
            res = new KVStorage<>(path, new SerializerForInteger(),
                    new SerializerForDouble());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        KVStorage<StudentKey, Student> res = null;
        try {
            res = new KVStorage<>(path, new SerializerForStudentKey(),
                    new SerializerForStudent());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        }
        return res;
    }


}
