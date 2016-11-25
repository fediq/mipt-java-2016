/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.mipt.java2016.homework.g595.kryloff.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 *
 * @author Kryloff Gregory
 */
public class JMyStudentKeySerializer implements JMySerializerInterface<StudentKey> {

    @Override
    public void serialize(RandomAccessFile raFile, StudentKey data) throws IOException {
        raFile.writeInt(data.getGroupId());
        raFile.writeUTF(data.getName());
    }

    @Override
    public StudentKey deSerialize(RandomAccessFile raFile) throws IOException {
        return new StudentKey(raFile.readInt(), raFile.readUTF());
    }
}
