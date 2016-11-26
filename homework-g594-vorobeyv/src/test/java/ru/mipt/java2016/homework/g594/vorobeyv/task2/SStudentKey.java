package ru.mipt.java2016.homework.g594.vorobeyv.task2;

/**
 * Created by Morell on 30.10.2016.
 */

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Morell on 30.10.2016.
 */

public class SStudentKey extends Serializator<StudentKey> {
    @Override
    public StudentKey read(DataInputStream input) throws IOException {
        int groupId = input.readInt();
        String name = input.readUTF();
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(DataOutputStream output, StudentKey value) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
    }
}
