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
 * @author KryloffGregoty
 */
public class JMyDoubleSerializer implements JMySerializerInterface<Double> {

    @Override
    public void serialize(RandomAccessFile raFile, Double data) throws IOException {
        raFile.writeDouble(data);
    }

    @Override
    public Double deSerialize(RandomAccessFile raFile) throws IOException {
        return raFile.readDouble();
    }
}
