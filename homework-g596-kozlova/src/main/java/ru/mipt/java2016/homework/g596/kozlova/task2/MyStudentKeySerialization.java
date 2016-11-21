package ru.mipt.java2016.homework.g596.kozlova.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyStudentKeySerialization implements MySerialization<StudentKey> {

    @Override
    public StudentKey read(DataInputStream readFromFile) throws IOException {
        int groupId = readFromFile.readInt();
        String name = readFromFile.readUTF();
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(DataOutputStream writeToFile, StudentKey student) throws IOException {
        writeToFile.writeInt(student.getGroupId());
        writeToFile.writeUTF(student.getName());
    }
}