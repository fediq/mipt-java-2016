package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.*;
import java.util.Date;

import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task2.Student;

public class SimpleSerializer {

    public static class SerializerForString implements Serializer<String> {

        @Override
        public void serializeToStream(String value, DataOutputStream outStream) throws IOException {
            outStream.writeUTF(value);
        }

        @Override
        public String deserializeFromStream(DataInputStream inputStream) throws IOException {
            return inputStream.readUTF();
        }
    }

    public static class SerializerForInteger implements Serializer<Integer> {

        @Override
        public void serializeToStream(Integer value, DataOutputStream outStream) throws IOException {
            outStream.writeInt(value);
        }

        @Override
        public Integer deserializeFromStream(DataInputStream inputStream) throws IOException {
            return inputStream.readInt();
        }
    }

    public static class SerializerForDouble implements Serializer<Double> {

        @Override
        public void serializeToStream(Double value, DataOutputStream outStream) throws IOException {
            outStream.writeDouble(value);
        }

        @Override
        public Double deserializeFromStream(DataInputStream inputStream) throws IOException {
            return inputStream.readDouble();
        }
    }

    public static class SerializerForStudentKey implements Serializer<StudentKey> {

        @Override
        public void serializeToStream(StudentKey value, DataOutputStream outStream) throws IOException {
            outStream.writeInt(value.getGroupId());
            outStream.writeUTF(value.getName());
        }

        @Override
        public StudentKey deserializeFromStream(DataInputStream inputStream) throws IOException {
            return new StudentKey(inputStream.readInt(), inputStream.readUTF());
        }
    }

    public static class SerializerForStudent implements Serializer<Student> {

        @Override
        public void serializeToStream(Student value, DataOutputStream outStream) throws IOException {
            outStream.writeInt(value.getGroupId());
            outStream.writeUTF(value.getName());
            outStream.writeUTF(value.getHometown());
            outStream.writeLong(value.getBirthDate().getTime());
            outStream.writeBoolean(value.isHasDormitory());
            outStream.writeDouble(value.getAverageScore());
        }

        @Override
        public Student deserializeFromStream(DataInputStream inputStream) throws IOException {
            return new Student(inputStream.readInt(), inputStream.readUTF(), inputStream.readUTF(),
                    new Date(inputStream.readLong()), inputStream.readBoolean(), inputStream.readDouble());
        }
    }
}
