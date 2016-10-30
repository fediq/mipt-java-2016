package ru.mipt.java2016.homework.g595.tkachenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 30/10/2016.
 */

public abstract class Serialization<valType> {

    abstract valType read(DataInputStream input) throws IOException;

    abstract void write(DataOutputStream output, valType x) throws IOException;

}
