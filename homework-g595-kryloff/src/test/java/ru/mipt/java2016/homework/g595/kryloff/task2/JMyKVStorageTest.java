package ru.mipt.java2016.homework.g595.kryloff.task2;
import java.io.IOException;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kryloff Gregory
 * @since 30.10.16
 */
public class JMyKVStorageTest extends AbstractSingleFileStorageTest {
    private final Map<String, Map<String, String>> stringMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Double>> numbersMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<StudentKey, Student>> pojoMaps = new ConcurrentHashMap<>();

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new JMyKVStorage<>(path, new JMyStringSerializer(), new JMyStringSerializer());
        } catch (RuntimeException | IOException ex) {
            Logger.getLogger(JMyKVStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new JMyKVStorage<>(path, new JMyIntegerSerializer(), new JMyDoubleSerializer());
        } catch (RuntimeException | IOException ex) {
            Logger.getLogger(JMyKVStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new JMyKVStorage<>(path, new JMyStudentKeySerializer(), new JMyStudentSerializer());
        } catch (RuntimeException | IOException ex) {
            Logger.getLogger(JMyKVStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    @Test
    //@Ignore
    public void testPersistAndCopy() {
        super.testPersistAndCopy();
    }
}
