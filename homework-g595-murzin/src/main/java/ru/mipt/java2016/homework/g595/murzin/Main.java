package ru.mipt.java2016.homework.g595.murzin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.Random;

/**
 * Created by dima on 05.11.16.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File temp = new File("temp");
        Random random = new Random();
        int[] data = new int[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextInt(256);
        }
        DataOutputStream output = new DataOutputStream(new FileOutputStream(temp));
        for (int b : data) {
            output.write(b);
        }

        RandomAccessFile file = new RandomAccessFile(temp, "r");
//        InputStream input = Channels.newInputStream(file.getChannel());
        DataInputStream input = new DataInputStream(Channels.newInputStream(file.getChannel()));
        for (int i = 0; i < data.length; i++) {
            int b = random.nextBoolean() ? input.read() : file.read();
            if (b != data[i]) {
                throw new RuntimeException();
            }
        }

//        RandomAccessFile randomAccessFile = new RandomAccessFile(temp, "r");
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(Channels.newInputStream(randomAccessFile.getChannel()));
//        DataInputStream input = new DataInputStream(bufferedInputStream);
//
//        System.out.println(randomAccessFile.getChannel().position());
//        System.out.println(Integer.toHexString(input.readInt()));
//        System.out.println(randomAccessFile.getChannel().position());
//        System.out.println(Integer.toHexString(randomAccessFile.readInt()));
//        System.out.println(Integer.toHexString(input.readInt()));
//        System.out.println(Integer.toHexString(randomAccessFile.readInt()));
//        System.out.println(Integer.toHexString(randomAccessFile.readInt()));
//        System.out.println(Integer.toHexString(input.readInt()));
//        System.out.println(Integer.toHexString(randomAccessFile.readInt()));
    }
}
