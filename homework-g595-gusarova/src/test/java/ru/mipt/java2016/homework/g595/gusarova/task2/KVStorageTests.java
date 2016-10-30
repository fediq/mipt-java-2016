package ru.mipt.java2016.homework.g595.gusarova.task2;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Дарья on 30.10.2016.
 */
public class KVStorageTests extends AbstractSingleFileStorageTest {
    private final Map<String, Map<String, String>> stringMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Double>> numbersMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<StudentKey, Student>> pojoMaps = new ConcurrentHashMap<>();

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KVStorage<String, String> temp;
        try {
            temp = new KVStorage<String, String>(path,
                    new SerializersAndDeserializers.SerializerAndDeserializerForString(),
                    new SerializersAndDeserializers.SerializerAndDeserializerForString());
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
                    new SerializersAndDeserializers.SerializerAndDeserializerForInteger(),
                    new SerializersAndDeserializers.SerializerAndDeserializerForDouble());
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
                    new SerializersAndDeserializers.SerializerAndDeserializerForStudentKey(),
                    new SerializersAndDeserializers.SerializerAndDeserializerForStudent());
        } catch (IOException exp) {
            return null;
        }
        return temp;
    }

    @Override
    @Test
    @Ignore
    public void testPersistAndCopy() {
        super.testPersistAndCopy();
    }
}
