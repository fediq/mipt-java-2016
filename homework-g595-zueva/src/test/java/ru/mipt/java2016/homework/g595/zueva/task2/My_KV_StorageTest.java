package ru.mipt.java2016.homework.g595.zueva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.g595.zueva.task2.task2.Specified_serializers;
import ru.mipt.java2016.homework.g595.zueva.task2.task2.My_KV_Storage;
import java.io.IOException;
import java.io.*;

/**
 * Created by maria on 26.10.16.
 */
public class My_KV_StorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        My_KV_Storage result = null;
        Specified_serializers.SerializerString a;
        Specified_serializers.SerializerString b;
        try {
            result = new My_KV_Storage(path, new Specified_serializers.SerializerString(),
                    new Specified_serializers.SerializerString());
        } catch (Exception except) {
            System.out.println(except.getMessage());
        }
        return result;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        My_KV_Storage<Integer, Double> result = null;
        try {
            result = new My_KV_Storage(path, new Specified_serializers.SerialiserInt(),
                    new Specified_serializers.SerializerDouble());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        My_KV_Storage result = null;
        try {
            result = new My_KV_Storage(path, new Specified_serializers.SerializerStudentKey(),
                    new Specified_serializers.SerializerStudent());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
