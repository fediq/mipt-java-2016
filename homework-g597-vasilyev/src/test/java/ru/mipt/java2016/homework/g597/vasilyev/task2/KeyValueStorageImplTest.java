package ru.mipt.java2016.homework.g597.vasilyev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;

/**
 * Created by mizabrik on 30.10.16.
 */
public class KeyValueStorageImplTest extends AbstractSingleFileStorageTest {
    private class StudentSerializer implements Serializer<Student> {
        public StudentSerializer() {
            this.stringSerializer = new StringSerializer();
        }

        @Override
        public void write(Student value, DataOutput destination) throws IOException {
            destination.writeInt(value.getGroupId());
            stringSerializer.write(value.getName(), destination);
            destination.writeLong(value.getBirthDate().getTime());
            stringSerializer.write(value.getHometown(), destination);
            destination.writeBoolean(value.isHasDormitory());
            destination.writeDouble(value.getAverageScore());
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

        private Serializer<String> stringSerializer;
    }

    private class StudentKeySerializer implements Serializer<StudentKey> {
        public StudentKeySerializer() {
            this.stringSerializer = new StringSerializer();
        }

        @Override
        public void write(StudentKey value, DataOutput destination) throws IOException {
            destination.writeInt(value.getGroupId());
            stringSerializer.write(value.getName(), destination);
        }

        @Override
        public StudentKey read(DataInput source) throws IOException {
            int groupId = source.readInt();
            String name = stringSerializer.read(source);
            return new StudentKey(groupId, name);
        }

        private Serializer<String> stringSerializer;
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        Path dbPath = FileSystems.getDefault().getPath(path, "strings.db");
        StringSerializer serializer = new StringSerializer();
        try {
            return new KeyValueStorageImpl<>(dbPath, serializer, serializer);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        Path dbPath = FileSystems.getDefault().getPath(path, "numbers.db");
        try {
            return new KeyValueStorageImpl<>(dbPath,
                    new IntegerSerializer(), new DoubleSerializer());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        Path dbPath = FileSystems.getDefault().getPath(path, "pojo.db");
        try {
            return new KeyValueStorageImpl<>(dbPath,
                    new StudentKeySerializer(), new StudentSerializer());
        } catch (IOException e) {
            return null;
        }
    }
}