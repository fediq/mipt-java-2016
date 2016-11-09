package ru.mipt.java2016.homework.g594.borodin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies.SerializationStrategy;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Maxim on 10/31/2016.
 */
public class StudentKeySerialization implements SerializationStrategy<StudentKey> {

    @Override
    public void serialize(StudentKey value, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(value.getGroupId());
        dataOutputStream.writeUTF(value.getName());
    }

    @Override
    public StudentKey deserialize(DataInputStream dataInputStream) throws IOException {
        int groupID = dataInputStream.readInt();
        String name = dataInputStream.readUTF();
        return new StudentKey(groupID, name);
    }
}
