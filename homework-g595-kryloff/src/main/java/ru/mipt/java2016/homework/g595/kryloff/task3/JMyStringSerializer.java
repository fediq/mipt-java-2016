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
public class JMyStringSerializer implements JMySerializerInterface<String> {

    @Override
    public void serialize(RandomAccessFile raFile, String data) throws IOException {
        raFile.writeUTF(data);
    }

    @Override
    public String deSerialize(RandomAccessFile raFile) throws IOException {
        return raFile.readUTF();
    }
}
