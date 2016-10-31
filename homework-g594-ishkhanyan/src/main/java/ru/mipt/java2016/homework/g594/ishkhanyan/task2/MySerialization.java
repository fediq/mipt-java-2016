package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;


interface MySerialization<Type> {
    void writeToFile(Type object, DataOutputStream file) throws IOException;

    Type readFromFile(DataInputStream file) throws IOException;

    MySerialization<Integer> MY_INT_SERIALIZATION = new MySerialization<Integer>() {
        @Override
        public void writeToFile(Integer object, DataOutputStream file) throws IOException {
            file.writeInt(object);
        }

        @Override
        public Integer readFromFile(DataInputStream file) throws IOException {
            return file.readInt();
        }
    };
    MySerialization<Double> MY_DOUBLE_SERIALIZATION = new MySerialization<Double>() {
        @Override
        public void writeToFile(Double object, DataOutputStream file) throws IOException {
            file.writeDouble(object);
        }

        @Override
        public Double readFromFile(DataInputStream file) throws IOException {
            return file.readDouble();
        }
    };
    MySerialization<String> MY_STRING_SERIALIZATION = new MySerialization<String>() {
        @Override
        public void writeToFile(String k, DataOutputStream file) throws IOException {
            file.writeUTF(k);
        }

        @Override
        public String readFromFile(DataInputStream file) throws IOException {
            return file.readUTF();
        }
    };
    MySerialization<StudentKey> MY_STUDENT_KEY_SERIALIZATION = new MySerialization<StudentKey>() {
        @Override
        public void writeToFile(StudentKey object, DataOutputStream file) throws IOException { // write all fields in series
            file.writeInt(object.getGroupId());
            file.writeUTF(object.getName());
        }

        @Override
        public StudentKey readFromFile(DataInputStream file) throws IOException { // read all fields and create object
            Integer groupId = file.readInt();
            String name = file.readUTF();
            return new StudentKey(groupId, name);
        }
    };
    MySerialization<Student> MY_STUDENT_SERIALIZATION = new MySerialization<Student>() { // similar to the previous type
        @Override
        public void writeToFile(Student object, DataOutputStream file) throws IOException {
            file.writeInt(object.getGroupId());
            file.writeUTF(object.getName());
            file.writeUTF(object.getHometown());
            file.writeLong(object.getBirthDate().getTime()); // date -> (long)milliseconds
            file.writeBoolean(object.isHasDormitory());
            file.writeDouble(object.getAverageScore());
        }

        @Override
        public Student readFromFile(DataInputStream file) throws IOException {
            Integer groupId = file.readInt();
            String name = file.readUTF();
            String homeTown = file.readUTF();
            Date date = new Date(file.readLong()); // (long)milliseconds -> date
            Boolean hasDormitory = file.readBoolean();
            Double averageScore = file.readDouble();
            return new Student(groupId, name, homeTown, date, hasDormitory, averageScore);
        }
    };
}

