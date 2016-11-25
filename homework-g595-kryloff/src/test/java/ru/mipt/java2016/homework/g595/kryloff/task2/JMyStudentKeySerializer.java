/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.mipt.java2016.homework.g595.kryloff.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 *
 * @author Kryloff Gregory
 */
public class JMyStudentKeySerializer implements JMySerializerInterface<StudentKey> {

    @Override
    public void serialize(DataOutputStream stream, StudentKey data) throws IOException {
        stream.writeInt(data.getGroupId());
        stream.writeUTF(data.getName());
    }

    @Override
    public StudentKey deSerialize(DataInputStream stream) throws IOException {
        return new StudentKey(stream.readInt(), stream.readUTF());
    }
}
