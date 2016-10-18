package ru.mipt.java2016.homework.g595.murzin.task2;

import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.GregorianCalendar;

import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;

/**
 * Created by Дмитрий Мурзин on 18.10.16.
 */
public class SimpleSingleFileStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new SimpleKeyValueStorage<>(path, String.class, String.class);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildI2DStorage(String path) {
        return new SimpleKeyValueStorage<>(path, Integer.class, Double.class);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildStudentsStorage(String path) {
        return new SimpleKeyValueStorage<>(path, StudentKey.class, Student.class);
    }

    @Test
    public void testStudents() {
        doInTempDirectory(path -> {
            KeyValueStorage<StudentKey, Student> storage = buildStudentsStorage(path);
            storage.write(new StudentKey(595, "murzin"), new Student(595, "murzin", "chel", new GregorianCalendar(1998, 8, 20).getTime(), true, 77));
            System.out.println(storage);
            storage.close();

            KeyValueStorage<StudentKey, Student> storage2 = buildStudentsStorage(path);
            Student student = storage2.read(new StudentKey(595, "murzin"));
            System.out.println(student.toString());
            System.out.println(student.getAverageScore());
            System.out.println(student.getBirthDate());
            System.out.println(student.getHometown());
        });
    }
}
