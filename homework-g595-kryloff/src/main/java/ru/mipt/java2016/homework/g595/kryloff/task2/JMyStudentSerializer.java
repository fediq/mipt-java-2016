/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.mipt.java2016.homework.g595.kryloff.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 *
 * @author Kryloff Gregory
 */
public class JMyStudentSerializer implements JMySerializerInterface<Student> {

    @Override
    public void serialize(DataOutputStream stream, Student data) throws IOException {
        stream.writeInt(data.getGroupId());
        stream.writeUTF(data.getName());
        stream.writeUTF(data.getHometown());
        stream.writeLong((data.getBirthDate()).getTime());
        stream.writeBoolean(data.isHasDormitory());
        stream.writeDouble(data.getAverageScore());
    }

    @Override
    public Student deSerialize(DataInputStream stream) throws IOException {
        return new Student(stream.readInt(), stream.readUTF(), stream.readUTF(),
                new Date(stream.readLong()), stream.readBoolean(), stream.readDouble());
    }
}
