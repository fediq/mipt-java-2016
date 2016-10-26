package ru.mipt.java2016.homework.g595.murzin.task2;

import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;

/**
 * Created by Дмитрий Мурзин on 18.10.16.
 */
public class SimpleSingleFileStorageTest extends AbstractSingleFileStorageTest {

    private static SerializationStrategy<StudentKey> FOR_STUDENT_KEY = new SerializationStrategy<StudentKey>() {
        @Override
        public void serializeToStream(StudentKey studentKey, DataOutputStream output) throws IOException {
            output.writeInt(studentKey.getGroupId());
            output.writeUTF(studentKey.getName());
        }

        @Override
        public StudentKey deserializeFromStream(DataInputStream input) throws IOException {
            return new StudentKey(input.readInt(), input.readUTF());
        }
    };

    private static SerializationStrategy<Student> FOR_STUDENT = new SerializationStrategy<Student>() {
        @Override
        public void serializeToStream(Student student, DataOutputStream output) throws IOException {
            output.writeInt(student.getGroupId());
            output.writeUTF(student.getName());
            output.writeUTF(student.getHometown());
            output.writeLong(student.getBirthDate().getTime());
            output.writeBoolean(student.isHasDormitory());
            output.writeDouble(student.getAverageScore());
        }

        @Override
        public Student deserializeFromStream(DataInputStream input) throws IOException {
            return new Student(input.readInt(), input.readUTF(), input.readUTF(), new Date(input.readLong()), input.readBoolean(), input.readDouble());
        }
    };

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new SimpleKeyValueStorage<>(path,
                SerializationStrategy.FOR_STRING,
                SerializationStrategy.FOR_STRING);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new SimpleKeyValueStorage<>(path,
                SerializationStrategy.FOR_INTEGER,
                SerializationStrategy.FOR_DOUBLE);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new SimpleKeyValueStorage<>(path, FOR_STUDENT_KEY, FOR_STUDENT);
    }

    @Test
    public void testStudents() {
        doInTempDirectory(path -> {
            KeyValueStorage<StudentKey, Student> storage = buildPojoStorage(path);
            storage.write(new StudentKey(595, "murzin"), new Student(595, "murzin", "chel", new GregorianCalendar(1998, 8, 20).getTime(), true, 77));
            storage.close();

            KeyValueStorage<StudentKey, Student> storage2 = buildPojoStorage(path);
            Student student = storage2.read(new StudentKey(595, "murzin"));
            assertEquals(student, new Student(595, "murzin", "chel", new GregorianCalendar(1998, 8, 20).getTime(), true, 77));
        });
    }

    @Test(expected = RuntimeException.class)
    public void testAccessToClosed() throws Exception {
        doInTempDirectory(path -> {
            KeyValueStorage<Integer, Double> storage = buildNumbersStorage(path);
            storage.write(1, 7.0);
            storage.write(2, .123);
            storage.write(3, 15.0);
            assertEquals((Double) .123, storage.read(2));
            storage.close();
            storage.read(2);
        });
    }
}