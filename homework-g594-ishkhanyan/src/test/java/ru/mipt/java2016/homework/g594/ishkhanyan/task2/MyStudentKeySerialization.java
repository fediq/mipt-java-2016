package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyStudentKeySerialization implements MySerialization<StudentKey> {

    @Override
    public void writeToFile(StudentKey object, DataOutputStream file) throws IOException {
        file.writeInt(object.getGroupId()); // write all fields in series
        file.writeUTF(object.getName());
    }

    @Override
    public StudentKey readFromFile(DataInputStream file) throws IOException {
        Integer groupId = file.readInt();
        String name = file.readUTF();
        return new StudentKey(groupId, name);
    }
}