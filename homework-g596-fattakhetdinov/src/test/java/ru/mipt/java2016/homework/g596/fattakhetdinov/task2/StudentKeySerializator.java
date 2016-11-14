package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.StudentKey;


public class StudentKeySerializator implements SerializationStrategy<StudentKey> {
    @Override
    public void serializeToFile(StudentKey studentKey, DataOutputStream output) throws IOException {
        output.writeInt(studentKey.getGroupId());
        output.writeUTF(studentKey.getName());
    }

    @Override
    public StudentKey deserializeFromFile(DataInputStream input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        return new StudentKey(groupId, name);
    }

    @Override
    public String getType() {
        return "StudentKey";
    }
}
