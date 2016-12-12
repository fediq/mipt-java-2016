package ru.mipt.java2016.homework.g596.kozlova.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MyStudentKeySerialization implements MySerialization<StudentKey> {
    @Override
    public void write(StudentKey obj, DataOutput output) throws IOException {
        output.writeInt(obj.getGroupId());
        output.writeUTF(obj.getName());
    }

    @Override
    public StudentKey read(DataInput input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        return new StudentKey(groupId, name);
    }
}