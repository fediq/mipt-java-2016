package ru.mipt.java2016.homework.g595.zueva.task2.task2;

/**
 * Created by nestyme on 30.10.2016.
 */

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class Serializers {

    public abstract class SerialiserInt implements SerializerStorage<Integer> {


        public void writeToStream(DataOutputStream out, Integer value) throws IOException {

            out.writeInt(value);

        }

        public Integer readFromStream(DataInputStream in) throws IOException {

            return in.readInt();
        }
    }


    public static class SerializerStorageString implements SerializerStorage<String> {

        public void writeToStream(DataOutputStream out, String value) throws IOException {
            out.writeUTF(value);
        }

        public String readFromStream(DataInputStream in) throws IOException {
            return in.readUTF();
        }
    }

    public static class SerializerStorageDouble implements SerializerStorage<Double> {

        public void writeToStream(DataOutputStream out, Double value) throws IOException {
            out.writeDouble(value);
        }

        @Override
        public Double readFromStream(DataInputStream in) throws IOException {
            return in.readDouble();
        }
    }
    class SerializerStorageStudentKey implements SerializerStorage<StudentKey> {

        public void writeToStream(DataOutputStream out, StudentKey value) throws IOException {
            out.writeInt(value.getGroupId());
            out.writeUTF(value.getName());
        }

        public StudentKey readFromStream(DataInputStream in) throws IOException {
            return new StudentKey(in.readInt(), in.readUTF());
        }
    }

    class SerializerStorageStudent implements SerializerStorage<Student> {

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