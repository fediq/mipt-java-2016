package ru.mipt.java2016.homework.g596.hromov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by igorhromov on 31.10.16.
 */
public class MySerializator {

    public static class SerializeForInteger implements Serializator<Integer> {

        @Override
        public void serializeToStream(Integer value, DataOutputStream outStream) throws IOException {
            outStream.writeInt(value);
        }

        @Override
        public Integer deserializeFromStream(DataInputStream inputStream) throws IOException {
            return inputStream.readInt();
        }
    }


    public static class SerializeForDouble implements Serializator<Double> {

        @Override
        public void serializeToStream(Double value, DataOutputStream outStream) throws IOException {
            outStream.writeDouble(value);
        }

        @Override
        public Double deserializeFromStream(DataInputStream inputStream) throws IOException {
            return inputStream.readDouble();
        }
    }

    public static class SerializeForString implements Serializator<String> {

        @Override
        public void serializeToStream(String value, DataOutputStream outStream) throws IOException {
            outStream.writeUTF(value);
        }

        @Override
        public String deserializeFromStream(DataInputStream inputStream) throws IOException {
            return inputStream.readUTF();
        }
    }

    public static class SerializeForStudent implements Serializator<Student> {

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

    public static class SerializeForStudentKey implements Serializator<StudentKey> {

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

}
