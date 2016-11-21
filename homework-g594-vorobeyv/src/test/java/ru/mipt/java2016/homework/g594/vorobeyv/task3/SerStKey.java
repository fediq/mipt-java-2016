package ru.mipt.java2016.homework.g594.vorobeyv.task3;

/**
 * Created by Morell on 30.10.2016.
 */

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by Morell on 30.10.2016.
 */

public class SerStKey extends OPSerializator<StudentKey> {
    @Override
    public StudentKey read(BufferedInputStream input) throws IOException {
        input.read(size.array());
        ByteBuffer buff = ByteBuffer.allocate(size.getInt(0));
        input.read( buff.array());
        buff.position(0);
        int groupId = buff.getInt();
        char c;
        StringBuilder str = new StringBuilder();
        while( ( c = buff.getChar()) != '\0')
        {
            str.append(c);
        }
        return new StudentKey( groupId, str.toString() );
    }

    @Override
    public int write(BufferedOutputStream output, StudentKey value) throws IOException {
        int intSize = Integer.SIZE/8 + 2 * ( value.getName().length() + 1 );
        size.putInt(0,intSize);
        ByteBuffer buff = ByteBuffer.allocate(intSize);
        buff.putInt(value.getGroupId());
        for( char c : value.getName().toCharArray()){
            buff.putChar(c);
        }
        buff.putChar('\0');
        output.write(size.array());
        output.write(buff.array());

        return intSize;
    }

    @Override
    public StudentKey randRead(RandomAccessFile input, long offset) throws IOException {
        input.seek(offset);

        int sizeByte = input.readInt();
        ByteBuffer buff = ByteBuffer.allocate(sizeByte);
        input.readFully(buff.array());
        buff.position(0);
        int groupId = buff.getInt();
        StringBuilder str = new StringBuilder();
        char c;
        while ( (c = buff.getChar() )!= '\0' ){
            str.append(c);
        }
        return new StudentKey( groupId, str.toString() );
    }
}
