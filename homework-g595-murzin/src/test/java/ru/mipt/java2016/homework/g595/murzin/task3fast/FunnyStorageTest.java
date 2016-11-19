package ru.mipt.java2016.homework.g595.murzin.task3fast;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by dima on 18.11.16.
 */
public class FunnyStorageTest extends KeyValueStoragePerformanceTest {
    private static final SerializationStrategy<StudentKey> FOR_STUDENT_KEY = new SerializationStrategy<StudentKey>() {
        @Override
        public void serializeToStream(StudentKey studentKey, DataOutput output) throws IOException {
            output.writeInt(studentKey.getGroupId());
            output.writeUTF(studentKey.getName());
        }

        @Override
        public StudentKey deserializeFromStream(DataInput input) throws IOException {
            return new StudentKey(input.readInt(), input.readUTF());
        }
    };

    private static final SerializationStrategy<Student> FOR_STUDENT = new SerializationStrategy<Student>() {
        @Override
        public void serializeToStream(Student student, DataOutput output) throws IOException {
            output.writeInt(student.getGroupId());
            output.writeUTF(student.getName());
            output.writeUTF(student.getHometown());
            output.writeLong(student.getBirthDate().getTime());
            output.writeBoolean(student.isHasDormitory());
            output.writeDouble(student.getAverageScore());
        }

        @Override
        public Student deserializeFromStream(DataInput input) throws IOException {
            return new Student(input.readInt(), input.readUTF(), input.readUTF(), new Date(input.readLong()), input.readBoolean(), input.readDouble());
        }
    };

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new FunnyStorage<>(path,
                SerializationStrategy.FOR_STRING,
                SerializationStrategy.FOR_STRING);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new FunnyStorage<>(path,
                SerializationStrategy.FOR_INTEGER,
                SerializationStrategy.FOR_DOUBLE);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new FunnyStorage<>(path,
                FOR_STUDENT_KEY,
                FOR_STUDENT);
    }
}
