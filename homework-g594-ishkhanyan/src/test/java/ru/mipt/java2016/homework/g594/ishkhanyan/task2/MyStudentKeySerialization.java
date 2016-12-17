package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class MyStudentKeySerialization implements MySerialization<StudentKey> {

    @Override
    public void writeToFile(StudentKey object, DataOutput file) throws IOException {
        file.writeInt(object.getGroupId()); // write all fields in series
        file.writeUTF(object.getName());
    }

    @Override
    public StudentKey readFromFile(DataInput file) throws IOException {
        Integer groupId = file.readInt();
        String name = file.readUTF();
        return new StudentKey(groupId, name);
    }
}