package ru.mipt.java2016.homework.g594.glebov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;


/**
 * Created by daniil on 31.10.16.
 */
public interface MySerializer<Type> {

    void streamSerialize(Type object, DataOutputStream output) throws IOException;

    Type streamDeserialize(DataInputStream input) throws IOException;

    MySerializer<String> STRING = new MySerializer<String>() {
        @Override
        public void streamSerialize(String object, DataOutputStream output) throws IOException {
            output.writeUTF(object);
        }

        @Override
        public String streamDeserialize(DataInputStream input) throws IOException {
            return input.readUTF();
        }
    };

    MySerializer<Integer> INT = new MySerializer<Integer>() {
        @Override
        public void streamSerialize(Integer object, DataOutputStream output) throws IOException {
            output.writeInt(object);
        }

        @Override
        public Integer streamDeserialize(DataInputStream input) throws IOException {
            return input.readInt();
        }
    };

    MySerializer<Double> DOUBLE = new MySerializer<Double>() {
        @Override
        public void streamSerialize(Double object, DataOutputStream output) throws IOException {
            output.writeDouble(object);
        }

        @Override
        public Double streamDeserialize(DataInputStream input) throws IOException {
            return input.readDouble();
        }
    };

    /*MySerializer<StudentKey> STUDENTKEY = new MySerializer<StudentKey>() {
        @Override
        public void streamSerialize(StudentKey object, DataOutputStream output) throws IOException {
            output.writeInt(object.getGroupId());
            output.writeUTF(object.getName());
        }

        @Override
        public StudentKey streamDeserialize(DataInputStream input) throws IOException {
            Integer groupId = input.readInt();
            String name = input.readUTF();
            StudentKey student = new StudentKey(groupId, name);
            return student;
        }
    };

    MySerializer<Student> STUDENT = new MySerializer<Student>() {
        @Override
        public void streamSerialize(Student object, DataOutputStream output) throws IOException {
            output.writeInt(object.getGroupId());
            output.writeUTF(object.getName());
            output.writeUTF(object.getHometown());
            output.writeLong(object.getBirthDate().getTime());
            output.writeBoolean(object.isHasDormitory());
            output.writeDouble(object.getAverageScore());
        }

        @Override
        public Student streamDeserialize(DataInputStream input) throws IOException {
            Integer groupId = input.readInt();
            String name = input.readUTF();
            String hometown = input.readUTF();
            Long ldate = input.readLong();
            Date date = new Date(ldate);
            Boolean hasDormitory = input.readBoolean();
            Double averageScore = input.readDouble();
            return new Student(groupId, name, hometown, date, hasDormitory, averageScore);
        }
    };*/
}
