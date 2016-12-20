package tests.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers.*;
import ru.mipt.java2016.homework.g595.efimochkin.task2.Storage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by sergejefimockin on 17.12.16.
 */
public class StorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new Storage<>(path, StringSerialization.getInstance(), StringSerialization.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new Storage<>(path, IntegerSerialization.getInstance(), DoubleSerialization.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new Storage<>(path, StudentKeySerialization.getInstance(), StudentSerialization.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
