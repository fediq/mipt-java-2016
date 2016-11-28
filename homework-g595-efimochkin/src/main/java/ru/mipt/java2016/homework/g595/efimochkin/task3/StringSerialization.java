package ru.mipt.java2016.homework.g595.efimochkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergeyefimockin on 28.11.16.
 */
public class StringSerialization implements BaseSerialization<String> {

    private static StringSerialization instance = new StringSerialization();

    public static StringSerialization getInstance() {return instance;}

    private StringSerialization() { }

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        try {
            file.writeUTF(object);
        }  catch (IOException e) {
        throw new IOException("Could not write to file.");
    }
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        try {
            return file.readUTF();
        }  catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }    }
