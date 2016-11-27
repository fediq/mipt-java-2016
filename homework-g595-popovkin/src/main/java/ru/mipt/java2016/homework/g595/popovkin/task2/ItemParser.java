package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Howl on 30.10.2016.
 */
interface ItemParser<T> {

    void serialize(T arg, FileOutputStream out) throws IOException;

    T deserialize(FileInputStream in) throws IOException;
}
