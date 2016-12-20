package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by Morell on 30.10.2016.
 */
public class SerString extends OPSerializator<String> {
    @Override
    public String read(BufferedInputStream input) throws IOException {
        input.read(size.array());
        // Здесь в длине уже учтен размер 2 * ( value.length() + 1 )
        int len = size.getInt(0);
        ByteBuffer buff = ByteBuffer.allocate(len);
        input.read(buff.array());
        char c;
        StringBuilder str = new StringBuilder();
        buff.position(0);
        while ((c = buff.getChar()) != '\0') {
            str.append(c);
        }
        return str.toString();
    }

    @Override
    public int write(BufferedOutputStream output, String value) throws IOException {
        size.putInt(0, 2 * (value.length() + 1));

        ByteBuffer buff = ByteBuffer.allocate(2 * (value.length() + 1));
        for (char sym : value.toCharArray()) {
            buff.putChar(sym);
        }
        buff.putChar('\0');
        output.write(size.array());
        output.write(buff.array());
        return 2 * (value.length() + 1);
    }

    @Override
    public String randRead(RandomAccessFile input, long offset) throws IOException {
        input.seek(offset);
        input.read(size.array());

        int size1 = size.getInt(0);
        ByteBuffer buff = ByteBuffer.allocate(size1);
        input.read(buff.array());
        char c;
        StringBuilder str = new StringBuilder();
        buff.position(0);
        while ((c = buff.getChar()) != '\0') {
            str.append(c);
        }
        return str.toString();
    }
}
