/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.mipt.java2016.homework.g595.kryloff.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Kryloff Gregory
 */
public class JMyIntegerSerializer implements JMySerializerInterface<Integer> {

    @Override
    public void serialize(RandomAccessFile raFile, Integer data) throws IOException {
        raFile.writeInt(data);
    }

    @Override
    public Integer deSerialize(RandomAccessFile raFile) throws IOException {
        return raFile.readInt();
    }
}
