package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

class StringSerializer implements Serializer<String> {
    @Override
    public String read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readUTF();
    }
    
    @Override
    public void write(RandomAccessFile file, String object, long position) throws IOException {
        file.seek(position);
        file.writeUTF(object);
    }
}
