package ru.mipt.java2016.homework.g594.petrov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by philipp on 30.10.16.
 */
public class SerializeStudent implements InterfaceSerialization<Student> {
    @Override
    public void writeValue(Student obj, DataOutputStream outputStream) throws IllegalStateException {
        try {
            outputStream.writeInt(obj.getGroupId());
            outputStream.writeUTF(obj.getName());
            outputStream.writeUTF(obj.getHometown());
            outputStream.writeLong(obj.getBirthDate().getTime());
            outputStream.writeBoolean(obj.isHasDormitory());
            outputStream.writeDouble(obj.getAverageScore());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Student readValue(DataInputStream inputStream) throws IllegalStateException {
        try {
            int studentGroup = inputStream.readInt();
            String studentName = inputStream.readUTF();
            String studentHometown = inputStream.readUTF();
            Date studentDate = new Date(inputStream.readLong());
            return new Student(studentGroup, studentName, studentHometown, studentDate,
                    inputStream.readBoolean(), inputStream.readDouble());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
