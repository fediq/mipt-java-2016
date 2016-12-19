package ru.mipt.java2016.homework.g595.gusarova.task3;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import ru.mipt.java2016.homework.g595.gusarova.task2.SerializerAndDeserializerForString;
import ru.mipt.java2016.homework.g595.gusarova.task2.SerializerAndDeserializerForInteger;
import ru.mipt.java2016.homework.g595.gusarova.task2.SerializerAndDeserializerForDouble;
import ru.mipt.java2016.homework.g595.gusarova.task2.SerializerAndDeserializerForStudent;
import ru.mipt.java2016.homework.g595.gusarova.task2.SerializerAndDeserializerForStudentKey;


/**
 * Created by Дарья on 20.11.2016.
 */
public class KVStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KVStorage<String, String> temp;
        try {
            temp = new KVStorage<String, String>(path,
                    new SerializerAndDeserializerForString(),
                    new SerializerAndDeserializerForString());
        }catch (MalformedDataException exp) {
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
        } catch (MalformedDataException exp) {
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
        } catch (MalformedDataException exp) {
            return null;
        }
        return temp;
    }
}
