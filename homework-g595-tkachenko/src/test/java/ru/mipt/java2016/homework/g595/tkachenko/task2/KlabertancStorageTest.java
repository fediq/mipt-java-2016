package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Dmitry on 30/10/2016.
 */
public class KlabertancStorageTest extends AbstractSingleFileStorageTest {

    public class StudentSerialization extends Serialization<Student> {
        @Override
        public Student read(DataInputStream input) throws IOException {
            return new Student(input.readInt(), readString(input), readString(input), new Date(input.readLong()),
                    input.readBoolean(), input.readDouble());
        }

        @Override
        public void write(DataOutputStream output, Student x) throws IOException {
            output.writeInt(x.getGroupId());
            writeString(output, x.getName());
            writeString(output, x.getHometown());
            output.writeLong(x.getBirthDate().getTime());
            output.writeBoolean(x.isHasDormitory());
            output.writeDouble(x.getAverageScore());
        }
    }

    public class StudentKeySerialization extends Serialization<StudentKey> {
        @Override
        public StudentKey read(DataInputStream input) throws IOException {
            return new StudentKey(input.readInt(), readString(input));
        }

        @Override
        public void write(DataOutputStream output, StudentKey x) throws IOException {
            output.writeInt(x.getGroupId());
            writeString(output, x.getName());
        }
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KlabertancStorage<>(path, new StringSerialization(), new StringSerialization());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KlabertancStorage<>(path, new IntSerialization(), new DoubleSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
       return new KlabertancStorage<>(path, new StudentKeySerialization(), new StudentSerialization());

    }
}
