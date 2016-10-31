package ru.mipt.java2016.homework.g596.bystrov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import static org.junit.Assert.assertEquals;

/**
 * Created by AlexBystrov.
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {

    private static SerializationStrategy<StudentKey> SS_STUDKEY =
            new SerializationStrategy<StudentKey>() {
                @Override
                public void serialize(StudentKey x, DataOutputStream out) throws IOException {
                    out.writeInt(x.getGroupId());
                    out.writeUTF(x.getName());
                }

                @Override
                public StudentKey deserialize(DataInputStream in) throws IOException {
                    return new StudentKey(in.readInt(), in.readUTF());
                }
            };

    private static SerializationStrategy<Student> SS_STUD = new SerializationStrategy<Student>() {
        @Override
        public void serialize(Student x, DataOutputStream out) throws IOException {
            out.writeInt(x.getGroupId());
            out.writeUTF(x.getName());
            out.writeUTF(x.getHometown());
            out.writeLong(x.getBirthDate().getTime());
            out.writeBoolean(x.isHasDormitory());
            out.writeDouble(x.getAverageScore());
        }

        @Override
        public Student deserialize(DataInputStream input) throws IOException {
            return new Student(input.readInt(), input.readUTF(), input.readUTF(),
                    new Date(input.readLong()), input.readBoolean(), input.readDouble());
        }
    };

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String fileName) {
        return new MyKeyValueStorage<>(SerializationStrategy.SS_STRING,
                SerializationStrategy.SS_STRING, fileName);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String fileName) {
        return new MyKeyValueStorage<>(SerializationStrategy.SS_INT, SerializationStrategy.SS_DOUBLE,
                fileName);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String fileName) {
        return new MyKeyValueStorage<>(SS_STUDKEY, SS_STUD, fileName);
    }
}
