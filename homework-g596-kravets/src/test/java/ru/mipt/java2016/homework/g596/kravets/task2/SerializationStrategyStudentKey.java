package ru.mipt.java2016.homework.g596.kravets.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class SerializationStrategyStudentKey implements MySerialization<StudentKey> {

    @Override
    public void write(DataOutputStream output, StudentKey data) throws IOException {
        output.writeInt(data.getGroupId());
        output.writeUTF(data.getName());
    }

    @Override
    public StudentKey read(DataInputStream input) throws IOException {
        Integer studentGroupID = input.readInt();
        String studentName = input.readUTF();
        return new StudentKey(studentGroupID, studentName);
    }
}
