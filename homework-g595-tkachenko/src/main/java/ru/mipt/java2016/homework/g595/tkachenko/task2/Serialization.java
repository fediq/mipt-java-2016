package ru.mipt.java2016.homework.g595.tkachenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 30/10/2016.
 */

public abstract class Serialization<valType> {

    public static void writeString(DataOutputStream output, String x) throws IOException {
        byte[] bytes = x.getBytes();
        output.writeInt(bytes.length);
        output.write(bytes, 0, bytes.length);
    }

    public static String readString(DataInputStream input) throws IOException {
        byte[] b = new byte[input.readInt()];
        input.read(b);
        return new String(b);
    }

    abstract valType read(DataInputStream input) throws IOException;

    abstract void write(DataOutputStream output, valType x) throws IOException;

}
