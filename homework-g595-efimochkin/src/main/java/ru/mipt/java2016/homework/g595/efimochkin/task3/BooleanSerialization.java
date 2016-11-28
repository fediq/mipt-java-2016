package ru.mipt.java2016.homework.g595.efimochkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;
//import ru.mipt.java2016.homework.g595.efimochkin.task3.BaseSerialization;

/**
 * Created by sergejefimockin on 28.11.16.
 */
public class BooleanSerialization implements BaseSerialization<Boolean> {

    private static BooleanSerialization instance = new BooleanSerialization();

    public static BooleanSerialization getInstance() {return instance;}

    private BooleanSerialization() { }

    @Override
    public void write(RandomAccessFile file, Boolean object) throws IOException {
        try {
            file.writeBoolean(object);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        try {
            return file.readBoolean();
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }
}
