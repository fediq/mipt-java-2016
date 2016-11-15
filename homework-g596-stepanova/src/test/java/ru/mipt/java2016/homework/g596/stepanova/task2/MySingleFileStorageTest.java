package ru.mipt.java2016.homework.g596.stepanova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;


public class MySingleFileStorageTest extends AbstractSingleFileStorageTest {

    SerializationStrategy<String> STRING_STRATEGY = new SerializationStrategy<String>() {
        @Override
        public void serializeToFile(String s, DataOutput output) throws IOException {
            output.writeUTF(s);
        }

        @Override
        public String deserializeFromFile(DataInput input) throws IOException {
            return input.readUTF();
        }
    };

    SerializationStrategy<Integer> INTEGER_STRATEGY = new SerializationStrategy<Integer>() {
        @Override
        public void serializeToFile(Integer integer, DataOutput output) throws IOException {
            output.writeInt(integer);
        }

        @Override
        public Integer deserializeFromFile(DataInput input) throws IOException {
            return input.readInt();
        }
    };

    SerializationStrategy<Double> DOUBLE_STRATEGY = new SerializationStrategy<Double>() {
        @Override
        public void serializeToFile(Double value, DataOutput output) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double deserializeFromFile(DataInput input) throws IOException {
            return input.readDouble();
        }
    };

    private static SerializationStrategy<StudentKey> STUDENT_KEY_STRATEGY =
            new SerializationStrategy<StudentKey>() {
                @Override
                public void serializeToFile(StudentKey studentKey, DataOutput output)
                        throws IOException {
                    output.writeInt(studentKey.getGroupId());
                    output.writeUTF(studentKey.getName());
                }

                @Override
                public StudentKey deserializeFromFile(DataInput input) throws IOException {
                    return new StudentKey(input.readInt(), input.readUTF());
                }
            };

    private static SerializationStrategy<Student> STUDENT_STRATEGY =
            new SerializationStrategy<Student>() {
                @Override
                public void serializeToFile(Student student, DataOutput output)
                        throws IOException {
                    output.writeInt(student.getGroupId());
                    output.writeUTF(student.getName());
                    output.writeUTF(student.getHometown());
                    output.writeLong(student.getBirthDate().getTime());
                    output.writeBoolean(student.isHasDormitory());
                    output.writeDouble(student.getAverageScore());
                }

                @Override
                public Student deserializeFromFile(DataInput input) throws IOException {
                    return new Student(input.readInt(), input.readUTF(), input.readUTF(),
                            new Date(input.readLong()), input.readBoolean(), input.readDouble());
                }
            };

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorage<>(path, STRING_STRATEGY, STRING_STRATEGY);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyKeyValueStorage<>(path, INTEGER_STRATEGY, DOUBLE_STRATEGY);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyKeyValueStorage<>(path, STUDENT_KEY_STRATEGY, STUDENT_STRATEGY);
    }
}