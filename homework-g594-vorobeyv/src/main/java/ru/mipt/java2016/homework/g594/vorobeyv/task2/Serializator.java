package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Morell on 30.10.2016.
 */
public abstract class Serializator<Type> {
    public abstract Type read(DataInputStream input) throws IOException;

    public abstract void write(DataOutputStream output, Type value) throws IOException;
}
