/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.mipt.java2016.homework.g595.kryloff.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Kryloff Gregory
 */
public class JMyStringSerializer implements JMySerializerInterface<String> {

    @Override
    public void serialize(DataOutputStream stream, String data) throws IOException {
        stream.writeUTF(data);
    }

    @Override
    public String deSerialize(DataInputStream stream) throws IOException {
        return stream.readUTF();
    }
}
