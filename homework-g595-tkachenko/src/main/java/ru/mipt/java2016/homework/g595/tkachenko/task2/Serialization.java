package ru.mipt.java2016.homework.g595.tkachenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 30/10/2016.
 */

public abstract class Serialization<valType> {

    public static void writeString(DataOutputStream output, String x) throws IOException {
        output.writeInt(x.length());
        output.write(x.getBytes(), 0, x.length());
    }

    public static String readString(DataInputStream input) throws IOException {
        byte[] b = new byte[input.readInt()];
        input.read(b);
        return new String(b);
    }

    abstract valType read(DataInputStream input) throws IOException;

    abstract void write(DataOutputStream output, valType x) throws IOException;

}
