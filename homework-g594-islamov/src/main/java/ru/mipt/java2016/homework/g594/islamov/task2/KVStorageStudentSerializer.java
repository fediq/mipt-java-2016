package ru.mipt.java2016.homework.g594.islamov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageStudentSerializer implements KVSSerializationInterface<Student> {
    @Override
    public void serialize(DataOutputStream out, Student object) throws IOException {
        out.writeInt(object.getGroupId());
        out.writeUTF(object.getName());
        out.writeUTF(object.getHometown());
        out.writeLong(object.getBirthDate().getTime());
        out.writeBoolean(object.isHasDormitory());
        out.writeDouble(object.getAverageScore());
    }

    @Override
    public Student deserialize(DataInputStream in) throws IOException {
        int groupID = in.readInt();
        String name = in.readUTF();
        String homeTown = in.readUTF();
        Date birthDate = new Date(in.readLong());
        boolean dormitory = in.readBoolean();
        double averageScore = in.readDouble();
        return new Student(groupID, name, homeTown, birthDate, dormitory, averageScore);
    }
}
