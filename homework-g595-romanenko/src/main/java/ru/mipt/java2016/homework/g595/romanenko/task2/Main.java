package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.time.Instant;
import java.util.Date;

/**
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println(Integer.MIN_VALUE);
        System.out.println(12421.124);
        System.out.println(Date.from(Instant.now()));
        System.out.println(Long.MAX_VALUE / 2);
        System.out.println("Hello, World! abacaba");

        System.out.println("\n\nTEST\n\n");

        RandomAccessFile file = new RandomAccessFile("temp.txt", "rw");
        OutputStream outputStream = Channels.newOutputStream(file.getChannel());

        SerializersFactory.IntegerSerializer.getInstance().serializeToStream(Integer.MIN_VALUE, outputStream);

        SerializersFactory.DoubleSerializer.getInstance().serializeToStream(12421.124, outputStream);

        SerializersFactory.DateSerializer.getInstance().serializeToStream(Date.from(Instant.now()), outputStream);

        SerializersFactory.LongSerializer.getInstance().serializeToStream(Long.MAX_VALUE / 2, outputStream);

        SerializersFactory.StringSerializer.getInstance().serializeToStream("Hello, World! abacaba", outputStream);

        outputStream.flush();
        file.close();




        file = new RandomAccessFile("temp.txt", "r");
        InputStream inputStream = Channels.newInputStream(file.getChannel());

        System.out.println(SerializersFactory.IntegerSerializer.getInstance().deserializeFromStream(inputStream));
        System.out.println(SerializersFactory.DoubleSerializer.getInstance().deserializeFromStream(inputStream));
        System.out.println(SerializersFactory.DateSerializer.getInstance().deserializeFromStream(inputStream));
        System.out.println(SerializersFactory.LongSerializer.getInstance().deserializeFromStream(inputStream));
        System.out.println(SerializersFactory.StringSerializer.getInstance().deserializeFromStream(inputStream));

        file.close();

    }
}
