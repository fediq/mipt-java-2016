package ru.mipt.java2016.homework.g595.gusarova.task2;


import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Дарья on 29.10.2016.
 */
public class SerializersAndDeserializers {
    public static class SerializerAndDeserializerForInteger implements SerializerAndDeserializer<Integer> {
        @Override
        public void serialize(Integer data, DataOutputStream stream) throws IOException {
            stream.writeInt(data);
        }

        @Override
        public Integer deserialize(DataInputStream stream) throws IOException {
            return stream.readInt();
        }
    }

    public static class SerializerAndDeserializerForDouble implements SerializerAndDeserializer<Double> {

        @Override
        public void serialize(Double data, DataOutputStream stream) throws IOException {
            stream.writeDouble(data);
        }

        @Override
        public Double deserialize(DataInputStream stream) throws IOException {
            return stream.readDouble();
        }
    }

    public static class SerializerAndDeserializerForBoolean implements SerializerAndDeserializer<Boolean> {

        @Override
        public void serialize(Boolean data, DataOutputStream stream) throws IOException {
            stream.writeBoolean(data);
        }

        @Override
        public Boolean deserialize(DataInputStream stream) throws IOException {
            return stream.readBoolean();
        }
    }

    public static class SerializerAndDeserializerForString implements SerializerAndDeserializer<String> {

        @Override
        public void serialize(String data, DataOutputStream stream) throws IOException {
            stream.writeUTF(data);
        }

        @Override
        public String deserialize(DataInputStream stream) throws IOException {
            return stream.readUTF();
        }
    }

    public static class SerializerAndDeserializerForStudentKey implements SerializerAndDeserializer<StudentKey> {

        @Override
        public void serialize(StudentKey data, DataOutputStream stream) throws IOException {
            stream.writeInt(data.getGroupId());
            stream.writeUTF(data.getName());
        }

        @Override
        public StudentKey deserialize(DataInputStream stream) throws IOException {
            StudentKey temp = new StudentKey(stream.readInt(), stream.readUTF());
            return temp;
        }
    }

    public static class SerializerAndDeserializerForStudent implements SerializerAndDeserializer<Student> {

        @Override
        public void serialize(Student data, DataOutputStream stream) throws IOException {
            stream.writeInt(data.getGroupId());
            stream.writeUTF(data.getName());
            stream.writeUTF(data.getHometown());
            stream.writeLong(data.getBirthDate().getTime());
            stream.writeBoolean(data.isHasDormitory());
            stream.writeDouble(data.getAverageScore());
        }

        @Override
        public Student deserialize(DataInputStream stream) throws IOException {
            Student temp = new Student(stream.readInt(), stream.readUTF(), stream.readUTF(),
                    new Date(stream.readLong()), stream.readBoolean(),
                    stream.readDouble());
            return temp;
        }
    }
}
