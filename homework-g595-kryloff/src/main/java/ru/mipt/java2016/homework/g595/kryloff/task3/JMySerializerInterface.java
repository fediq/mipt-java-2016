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
/**
 *
 * @author Kryloff Gregory
 * @param <V>
 */
public interface JMySerializerInterface<V> {

    void serialize(RandomAccessFile raFile, V value) throws IOException;

    V deSerialize(RandomAccessFile raFile) throws IOException;
}
