package ru.mipt.java2016.homework.g595.gusarova.task2;

/**
 * Created by Дарья on 17.12.2016.
 */

import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SerializerAndDeserializerForStudentKey implements SerializerAndDeserializer<StudentKey> {
    @Override
    public void serialize(StudentKey data, DataOutput file) throws IOException {
        file.writeInt(data.getGroupId());
        file.writeUTF(data.getName());
    }

    @Override
    public StudentKey deserialize(DataInput file) throws IOException {
        StudentKey temp = new StudentKey(file.readInt(), file.readUTF());
        return temp;
    }
}
