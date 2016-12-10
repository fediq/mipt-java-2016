package tests.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.efimochkin.task3.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStorageFactories;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

/**
 * Created by sergejefimockin on 28.11.16.
 */
public class StoragePerfomanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        //try {
        Storage storage = new Storage<String, String>(path, StringSerialization.getInstance(), StringSerialization.getInstance());
        return storage;
        /*catch (IOException e)
        {
            throw new MalformedDataException("Something went wrong");
        }*/
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        //try {
        Storage storage = new Storage<Integer, Double>(path, IntegerSerialization.getInstance(), DoubleSerialization.getInstance());
        return storage;
        /*catch (IOException e)
        {
            throw new MalformedDataException("Something went wrong");
        }*/
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        //try{
        Storage storage = new Storage<StudentKey, Student >(path, StudentKeySerialization.getInstance(), StudentSerialization.getInstance());
        return storage;
   /* }
        catch (IOException e)
        {
            throw new MalformedDataException("Something went wrong");
        }
    }*/
    }}