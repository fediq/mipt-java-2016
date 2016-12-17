package ru.mipt.java2016.homework.g594.islamov.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageStudentKeySerializer implements KVSSerializationInterface<StudentKey> {
    @Override
    public void serialize(DataOutputStream out, StudentKey object) throws IOException {
        out.writeInt(object.getGroupId());
        out.writeUTF(object.getName());
    }

    @Override
    public StudentKey deserialize(DataInputStream in) throws IOException {
        Integer groupID = in.readInt();
        String name = in.readUTF();
        return new StudentKey(groupID, name);
    }
}