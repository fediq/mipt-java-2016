package ru.mipt.java2016.homework.g594.sharuev.task2;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sharik on 10/21/16.
 */
class SimpleStringSerializer implements SerializationStrategy<String> {


    @Override
    public byte[] serializeToBytes(String s) throws SerializationException {
        return new byte[0];
    }

    @Override
    public void serializeToStream(String s,
                                  OutputStream outputStream) throws SerializationException {
        try {
            DataOutputStream dos = new DataOutputStream(outputStream);

            dos.writeLong(s.length());
            dos.writeUTF(s);
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

    @Override
    public String deserializeFromStream(InputStream inputStream) throws SerializationException {
        try {
            DataInputStream dis = new DataInputStream(inputStream);
            long length = dis.readLong();
            return dis.readUTF();
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

    @Override
    public String deserialize(byte[] bytes, int offset) throws SerializationException {
        return null;
    }
}

class SimpleIntegerSerializer implements SerializationStrategy<Integer> {


    @Override
    public void serializeToStream(Integer s,
                                  OutputStream outputStream) throws SerializationException {
        try {
            DataOutputStream dos = new DataOutputStream(outputStream);

            dos.writeInt(s);
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

    @Override
    public Integer deserializeFromStream(InputStream inputStream) throws SerializationException {
        try {
            DataInputStream dis = new DataInputStream(inputStream);
            return dis.readInt();
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

}

class SimpleStudentSerializer implements SerializationStrategy<Student> {

    @Override
    public void serializeToStream(Student s,
                                  OutputStream outputStream) throws SerializationException {
        try {
            DataOutputStream dos = new DataOutputStream(outputStream);

            dos.writeUTF();
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

    @Override
    public Student deserializeFromStream(InputStream inputStream) throws SerializationException {
        try {
            DataInputStream dis = new DataInputStream(inputStream);
            String hometown;
            Date birthDate;
            boolean hasDormitory;
            double averageScore;
            hometown = dis.readUTF();
            try {
                birthDate = new SimpleDateFormat("dd.MM.yyyy").parse(dis.readUTF());
            } catch (ParseException e) {
                throw new SerializationException("Invalid date");
            }
            hasDormitory =
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }
}

class SimpleStudentKeySerializer implements SerializationStrategy<StudentKey> {

    @Override
    public void serializeToStream(StudentKey s,
                                  OutputStream outputStream) throws SerializationException {
        try {
            DataOutputStream dos = new DataOutputStream(outputStream);

            dos.writeInt(s.getGroupId());
            dos.writeUTF(s.getName());
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

    @Override
    public StudentKey deserializeFromStream(InputStream inputStream) throws SerializationException {
        try {
            DataInputStream dis = new DataInputStream(inputStream);
            int groupId;
            String name;
            groupId = dis.readInt();
            name = dis.readUTF();
            return new StudentKey(groupId, name);
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

}