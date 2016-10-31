package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Morell on 30.10.2016.
 */
public class SString extends Serializator<String> {
    @Override
    public String read(DataInputStream input) throws IOException {
        try {
            return input.readUTF();
        } catch (IOException ex) {
            throw ex;
        }
    }

    @Override
    public void write(DataOutputStream output, String value) throws IOException {
        try {
            output.writeUTF(value);
        } catch (IOException ex) {
            throw ex;
        }
    }
}
