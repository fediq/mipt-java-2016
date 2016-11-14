package ru.mipt.java2016.homework.g595.belyh.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.Student;
import java.sql.Date;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by white2302 on 29.10.2016.
 */
public class MySerializer {
    public static class StringSerializer implements Serializer<String> {
        @Override
        public void serialize(String value, DataOutputStream stream) throws IOException {
            stream.writeUTF(value);
        }

        @Override
        public String deserialize(DataInputStream stream) throws IOException {
            return stream.readUTF();
        }
    }

    public static class IntegerSerializer implements Serializer<Integer> {
        @Override
        public void serialize(Integer value, DataOutputStream stream) throws IOException {
            stream.writeInt(value);
        }

        @Override
        public Integer deserialize(DataInputStream stream) throws IOException {
            return stream.readInt();
        }
    }

    public static class DoubleSerializer implements Serializer<Double> {
        @Override
        public void serialize(Double value, DataOutputStream stream) throws IOException {
            stream.writeDouble(value);
        }

        @Override
        public Double deserialize(DataInputStream stream) throws IOException {
            return stream.readDouble();
        }
    }

    public static class StudentSerializer implements Serializer<Student> {
        @Override
        public void serialize(Student value, DataOutputStream stream) throws IOException {
            stream.writeInt(value.getGroupId());
            stream.writeUTF(value.getName());
            stream.writeUTF(value.getHometown());
            stream.writeLong(value.getBirthDate().getTime());
            stream.writeBoolean(value.isHasDormitory());
            stream.writeDouble(value.getAverageScore());
        }

        @Override
        public Student deserialize(DataInputStream stream) throws IOException {
            return new Student(stream.readInt(),
                    stream.readUTF(),
                    stream.readUTF(),
                    new Date(stream.readLong()),
                    stream.readBoolean(),
                    stream.readDouble()
                    );
        }
    }

    public static class StudentKeySerializer implements Serializer<StudentKey> {
        @Override
        public void serialize(StudentKey value, DataOutputStream stream) throws IOException {
            stream.writeInt(value.getGroupId());
            stream.writeUTF(value.getName());
        }

        @Override
        public StudentKey deserialize(DataInputStream stream) throws IOException {
            return new StudentKey(stream.readInt(), stream.readUTF());
        }
    }
}
