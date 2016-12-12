package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.*;

/**
 * Created by Dmitry on 20/11/2016.
 */

public abstract class Serialization<valType> {

    abstract valType read(DataInput input) throws IOException;

    abstract void write(DataOutput output, valType x) throws IOException;

}
