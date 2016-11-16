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
public class JMyIntegerSerializer implements JMySerializerInterface<Integer> {

    @Override
    public void serialize(DataOutputStream stream, Integer data) throws IOException {
        stream.writeInt(data);
    }

    @Override
    public Integer deSerialize(DataInputStream stream) throws IOException {
        return stream.readInt();
    }
}
