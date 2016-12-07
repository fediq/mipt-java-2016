package ru.mipt.java2016.homework.g597.vasilyev.tasks2and3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by mizabrik on 30.10.16.
 */
public class KeyValueStorageImplTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        StringSerializer serializer = new StringSerializer();
        try {
            return new KeyValueStorageImpl<>(path, serializer, serializer);
        } catch (Exception e) {
            throw new RuntimeException("Could not build storage", e);
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new KeyValueStorageImpl<>(path,
                    new IntegerSerializer(), new DoubleSerializer());
        } catch (Exception e) {
            throw new RuntimeException("Could not build storage", e);
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new KeyValueStorageImpl<>(path,
                    new StudentKeySerializer(), new StudentSerializer());
        } catch (Exception e) {
            throw new RuntimeException("Could not build storage", e);
        }
    }

    private class StudentSerializer implements Serializer<Student> {
        private final Serializer<String> stringSerializer;

        public StudentSerializer() {
            this.stringSerializer = new StringSerializer();
        }

        @Override
        public int write(Student value, DataOutput destination) throws IOException {
            int size = 21;
            destination.writeInt(value.getGroupId());
            size += stringSerializer.write(value.getName(), destination);
            destination.writeLong(value.getBirthDate().getTime());
            size += stringSerializer.write(value.getHometown(), destination);
            destination.writeBoolean(value.isHasDormitory());
            destination.writeDouble(value.getAverageScore());
            return size;
        }

        @Override
        public Student read(DataInput source) throws IOException {
            int groupId = source.readInt();
            String name = stringSerializer.read(source);
            Date birthDate = new Date(source.readLong());
            String hometown = stringSerializer.read(source);
            boolean hasDormitory = source.readBoolean();
            double averageScore = source.readDouble();
            return new Student(groupId, name, hometown, birthDate,
                    hasDormitory, averageScore);
        }
    }

    private class StudentKeySerializer implements Serializer<StudentKey> {
        private final Serializer<String> stringSerializer;

        public StudentKeySerializer() {
            this.stringSerializer = new StringSerializer();
        }

        @Override
        public int write(StudentKey value, DataOutput destination) throws IOException {
            destination.writeInt(value.getGroupId());
            return 4 + stringSerializer.write(value.getName(), destination);
        }

        @Override
        public StudentKey read(DataInput source) throws IOException {
            int groupId = source.readInt();
            String name = stringSerializer.read(source);
            return new StudentKey(groupId, name);
        }
    }
}