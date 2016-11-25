/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.mipt.java2016.homework.g595.kryloff.task3;

import ru.mipt.java2016.homework.g595.kryloff.task2.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 *
 * @author Kryloff Gregory
 */
public class JMyStudentSerializer implements JMySerializerInterface<Student> {

    @Override
    public void serialize(RandomAccessFile raFile, Student data) throws IOException {
        raFile.writeInt(data.getGroupId());
        raFile.writeUTF(data.getName());
        raFile.writeUTF(data.getHometown());
        raFile.writeLong((data.getBirthDate()).getTime());
        raFile.writeBoolean(data.isHasDormitory());
        raFile.writeDouble(data.getAverageScore());
    }

    @Override
    public Student deSerialize(RandomAccessFile raFile) throws IOException {
        return new Student(raFile.readInt(), raFile.readUTF(), raFile.readUTF(),
                new Date(raFile.readLong()), raFile.readBoolean(), raFile.readDouble());
    }
}
