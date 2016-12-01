package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Morell on 30.10.2016.
 */
public class SInteger extends Serializator<Integer> {
    @Override
    public Integer read(DataInputStream input) throws IOException {
        try {
            return input.readInt();
        } catch (IOException ex) {
            throw ex;
        }
    }

    @Override
    public void write(DataOutputStream output, Integer value) throws IOException {
        try {
            output.writeInt(value);
        } catch (IOException ex) {
            throw ex;
        }
    }
}
