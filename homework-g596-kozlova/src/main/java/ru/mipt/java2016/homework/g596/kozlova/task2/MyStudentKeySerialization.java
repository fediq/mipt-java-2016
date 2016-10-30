package ru.mipt.java2016.homework.g596.kozlova.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyStudentKeySerialization extends MySerialization<StudentKey> {

    @Override
    public StudentKey read(DataInputStream read_from_file) throws IOException {
        Integer groupId = read_from_file.readInt();
        String name = read_from_file.readUTF();
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(DataOutputStream write_to_file, StudentKey student) throws IOException {
        write_to_file.writeInt(student.getGroupId());
        write_to_file.writeUTF(student.getName());
    }
}