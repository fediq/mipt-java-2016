package ru.mipt.java2016.homework.g595.rodin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.rodin.task2.Serializer.*;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by dmitry on 28.10.16.
 */
public class KeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        CSerializeString firstSerializer = new CSerializeString();
        CSerializeString seconfSerializer = new CSerializeString();
        CKeyValueStorage<String,String> result = null;
        result = new CKeyValueStorage<>(path
                ,firstSerializer
                ,seconfSerializer
                ,"String"
                ,"String");
        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        CSerializeInteger keySerializer = new CSerializeInteger();
        CSerializeDouble valueSerializer = new CSerializeDouble();
        CKeyValueStorage<Integer,Double> result = null;
        result = new CKeyValueStorage<>(path
                ,keySerializer
                ,valueSerializer
                ,"Integer"
                ,"Double");
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        CSerializeStudentKey keySerializer = new CSerializeStudentKey();
        CSerializeStudent valueSerializer = new CSerializeStudent();
        CKeyValueStorage<StudentKey,Student> result = null;
        result = new CKeyValueStorage<>(path
                ,keySerializer
                ,valueSerializer
                ,"StudentKey"
                ,"Student");
        return result;
    }
}
