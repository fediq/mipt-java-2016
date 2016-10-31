package ru.mipt.java2016.homework.g595.zueva.task2;

/**
 * Created by nestyme on 30.10.2016.
 */
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.*;
import java.util.Date;

public class Specified_serializers{

    public static class SerialiserInt implements Serializer<Integer> {


        public void writeToStream(DataOutputStream out, Integer value) throws IOException {

            out.writeInt(value);

        }

        public Integer readFromStream(DataInputStream in) throws IOException {

            return in.readInt();
        }
    }


    public static class SerializerString implements Serializer<String> {

        public void writeToStream(DataOutputStream out, String value) throws IOException {
            out.writeUTF(value);
        }

        public String readFromStream(DataInputStream in) throws IOException {
            return in.readUTF();
        }
    }

    public static class SerializerDouble implements Serializer<Double> {

        public void writeToStream(DataOutputStream out, Double value) throws IOException {
            out.writeDouble(value);
        }

        @Override
        public Double readFromStream(DataInputStream in) throws IOException {
            return in.readDouble();
        }
    }

    public static class SerializerStudentKey implements Serializer<StudentKey> {

        public void writeToStream(DataOutputStream out, StudentKey value) throws IOException {
            out.writeInt(value.getGroupId());
            out.writeUTF(value.getName());
        }

        public StudentKey readFromStream(DataInputStream in) throws IOException {
            return new StudentKey(in.readInt(), in.readUTF());
        }
    }

    public static class SerializerStudent implements Serializer<Student> {

        public void writeToStream(DataOutputStream out, Student value) throws IOException {
            out.writeInt(value.getGroupId());
            out.writeUTF(value.getName());
            out.writeUTF(value.getHometown());
            out.writeLong(value.getBirthDate().getTime());
            out.writeBoolean(value.isHasDormitory());
            out.writeDouble(value.getAverageScore());

        }

        public Student readFromStream(DataInputStream in) throws IOException {
            return new Student(in.readInt(), in.readUTF(), in.readUTF(),
                               new Date(in.readLong()), in.readBoolean(),
                               in.readDouble());
        }
    }
}
