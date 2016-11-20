package ru.mipt.java2016.homework.g597.shirokova.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

class ConcreteSerializationStrategy {

    static class StringConcreteStrategy implements SerializationStrategy<String> {
        @Override
        public void serialize(DataOutputStream output, String value) throws IOException {
            output.writeUTF(value);
        }

        @Override
        public String deserialize(DataInputStream input) throws IOException {
            return input.readUTF();
        }
    }

    static class IntegerConcreteStrategy implements SerializationStrategy<Integer> {
        @Override
        public void serialize(DataOutputStream output, Integer value) throws IOException {
            output.writeInt(value);
        }

        @Override
        public Integer deserialize(DataInputStream input) throws IOException {
            return input.readInt();
        }
    }

    static class DoubleConcreteStrategy implements SerializationStrategy<Double> {
        @Override
        public void serialize(DataOutputStream output, Double value) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double deserialize(DataInputStream input) throws IOException {
            return input.readDouble();
        }
    }

    static class StudentKeyConcreteStrategy implements SerializationStrategy<StudentKey> {
        @Override
        public void serialize(DataOutputStream output, StudentKey key) throws IOException {
            output.writeInt(key.getGroupId());
            output.writeUTF(key.getName());
        }

        @Override
        public StudentKey deserialize(DataInputStream input) throws IOException {
            return new StudentKey(
                    input.readInt(), //GroupId
                    input.readUTF() //Name
            );
        }
    }

    static class StudentConcreteStrategy implements SerializationStrategy<Student> {
        @Override
        public void serialize(DataOutputStream output, Student value) throws IOException {
            output.writeInt(value.getGroupId());
            output.writeUTF(value.getName());
            output.writeUTF(value.getHometown());
            output.writeLong(value.getBirthDate().getTime());
            output.writeBoolean(value.isHasDormitory());
            output.writeDouble(value.getAverageScore());
        }

        @Override
        public Student deserialize(DataInputStream input) throws IOException {
            return new Student(
                    input.readInt(), // GroupId
                    input.readUTF(), // Name
                    input.readUTF(), // Hometown
                    new Date(input.readLong()), // Birth date
                    input.readBoolean(), // Dormitory
                    input.readDouble() //Score
            );
        }
    }
}
