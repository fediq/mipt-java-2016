package ru.mipt.java2016.homework.g595.zueva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;


/**
 * Created by maria on 26.10.16.
 */
public class My_KV_StorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        MyKVStorage result = null;
        Serializers.SerializerString a;
        Serializers.SerializerString b;
        try {
            result = new MyKVStorage(path, new Serializers.SerializerString(),
                    new Serializers.SerializerString());
        } catch (Exception except) {
            System.out.println(except.getMessage());
        }
        return result;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MyKVStorage<Integer, Double> result = null;
        try {
            result = new MyKVStorage(path, new Serializers.SerialiserInt(),
                    new Serializers.SerializerDouble());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        MyKVStorage result = null;
        try {
            result = new MyKVStorage(path, new Serializers.SerializerStudentKey(),
                    new Serializers.SerializerStudent());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
