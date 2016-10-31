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
        char[] c = x.toCharArray();
        for (int i = 0; i < x.length(); i++) {
            output.writeInt((int) c[i]);
        }
    }

    public static String readString(DataInputStream input) throws IOException {
        StringBuilder sBuild = new StringBuilder();
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            sBuild.append((char) input.readInt());
        }
        return sBuild.toString();
    }

    abstract valType read(DataInputStream input) throws IOException;

    abstract void write(DataOutputStream output, valType x) throws IOException;

}
