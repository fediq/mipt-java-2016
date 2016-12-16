package ru.mipt.java2016.homework.g595.nosareva.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.nosareva.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import javax.xml.bind.ValidationException;
import java.io.IOException;

/**
 * Created by maria on 19.11.16.
 */
public class OptimizedKVSTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        OptimizedKVStorage<String, String> res = null;
        try {
            res = new OptimizedKVStorage<>(path, new SerializerForString(),
                    new SerializerForString());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        } catch (ValidationException exc) {
            System.out.println(exc.getMessage());
        }
        return res;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        OptimizedKVStorage<Integer, Double> res = null;
        try {
            res = new OptimizedKVStorage<>(path, new SerializerForInteger(),
                    new SerializerForDouble());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        } catch (ValidationException exc) {
            System.out.println(exc.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        OptimizedKVStorage<StudentKey, Student> res = null;
        try {
            res = new OptimizedKVStorage<>(path, new SerializerForStudentKey(),
                    new SerializerForStudent());
        } catch (IOException excep) {
            System.out.println(excep.getMessage());
        } catch (ValidationException exc) {
            System.out.println(exc.getMessage());
        }
        return res;
    }
}

