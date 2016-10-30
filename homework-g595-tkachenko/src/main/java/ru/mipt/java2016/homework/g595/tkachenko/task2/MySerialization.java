package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public interface MySerialization<valType> {

    valType readSerialize(DataInputStream input) throws IOException;

    void writeSerialize(valType value, DataOutputStream output) throws IOException;

    MySerialization<Integer> INT_STRATEGY = new MySerialization<Integer>() {
        @Override
        public void writeSerialize(Integer x, DataOutputStream output) throws IOException {
            output.writeInt(x);
        }

        @Override
        public Integer readSerialize(DataInputStream input) throws IOException {
            return input.readInt();
        }
    };

    MySerialization<Double> DOUBLE_STRATEGY = new MySerialization<Double>() {
        @Override
        public void writeSerialize(Double d, DataOutputStream output) throws IOException {
            output.writeDouble(d);
        }

        @Override
        public Double readSerialize(DataInputStream input) throws IOException {
            return input.readDouble();
        }
    };

    MySerialization<String> STRING_STRATEGY = new MySerialization<String>() {
        @Override
        public void writeSerialize(String s, DataOutputStream output) throws IOException {
            output.writeUTF(s);
        }

        @Override
        public String readSerialize(DataInputStream input) throws IOException {
            return input.readUTF();
        }
    };

    MySerialization<Student> STUDENT_STRATEGY = new MySerialization<Student>() {
        @Override
        public void writeSerialize(Student student, DataOutputStream output) throws IOException {
            output.writeInt(student.getGroupId());
            output.writeUTF(student.getName());
            output.writeUTF(student.getHometown());
            output.writeLong(student.getBirthDate().getTime());
            output.writeBoolean(student.isHasDormitory());
            output.writeDouble(student.getAverageScore());
        }

        @Override
        public Student readSerialize(DataInputStream input) throws IOException {
            return new Student(input.readInt(), input.readUTF(), input.readUTF(), new Date(input.readLong()),
                    input.readBoolean(), input.readDouble());
        }
    };

    MySerialization<StudentKey> STUDENT_KEY_STRATEGY = new MySerialization<StudentKey>() {
        @Override
        public void writeSerialize(StudentKey studentKey, DataOutputStream output) throws IOException {
            output.writeInt(studentKey.getGroupId());
            output.writeUTF(studentKey.getName());
        }

        @Override
        public StudentKey readSerialize(DataInputStream input) throws IOException {
            return new StudentKey(input.readInt(), input.readUTF());
        }
    };
}
