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
public class JMyLongSerializer implements JMySerializerInterface<Long> {

    @Override
    public void serialize(RandomAccessFile raFile, Long data) throws IOException {
        raFile.writeLong(data);
    }

    @Override
    public Long deSerialize(RandomAccessFile raFile) throws IOException {
        return raFile.readLong();
    }
}
