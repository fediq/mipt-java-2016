package ru.mipt.java2016.homework.g595.belyh.task3;

import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.RandomAccessFile;
import java.sql.Date;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by white2302 on 29.10.2016.
 */
public class MyStundentSerializer {
    public static class StudentSerializer implements Serializer<Student> {
        @Override
        public void serialize(Student value, RandomAccessFile f) throws IOException {
            f.writeInt(value.getGroupId());
            f.writeUTF(value.getName());
            f.writeUTF(value.getHometown());
            f.writeLong(value.getBirthDate().getTime());
            f.writeBoolean(value.isHasDormitory());
            f.writeDouble(value.getAverageScore());
        }

        @Override
        public Student deserialize(RandomAccessFile f) throws IOException {
            return new Student(f.readInt(),
                    f.readUTF(),
                    f.readUTF(),
                    new Date(f.readLong()),
                    f.readBoolean(),
                    f.readDouble()
            );
        }
    }

    public static class StudentKeySerializer implements Serializer<StudentKey> {
        @Override
        public void serialize(StudentKey value, RandomAccessFile f) throws IOException {
            f.writeInt(value.getGroupId());
            f.writeUTF(value.getName());
        }

        @Override
        public StudentKey deserialize(RandomAccessFile f) throws IOException {
            return new StudentKey(f.readInt(), f.readUTF());
        }
    }
}
