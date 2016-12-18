package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.StudentKey;


public class StudentKeySerializator implements SerializationStrategy<StudentKey> {
    @Override
    public void serializeToFile(StudentKey studentKey, DataOutput output) throws IOException {
        output.writeInt(studentKey.getGroupId());
        output.writeUTF(studentKey.getName());
    }

    @Override
    public StudentKey deserializeFromFile(DataInput input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        return new StudentKey(groupId, name);
    }

    @Override
    public String getType() {
        return "StudentKey";
    }
}