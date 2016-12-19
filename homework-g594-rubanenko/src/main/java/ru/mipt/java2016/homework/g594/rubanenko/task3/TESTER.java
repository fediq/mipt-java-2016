package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.io.IOException;

/**
 * Created by king on 10.12.16.
 */
public class TESTER {
    public static void main(String[] args) {
        FastKeyValueStorageImpl<Integer, Integer> fst =
                null;
        try {
            fst = new FastKeyValueStorageImpl("/home/king", new FastIntegerSerializer(), new FastIntegerSerializer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fst.write(5, 5);
        FastKeyValueStorageImpl<Integer, Integer> snd =
                null;
        try {
            snd = new FastKeyValueStorageImpl("/home/king", new FastIntegerSerializer(), new FastIntegerSerializer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        snd.write(6, 6);
        snd.write(5, 6);
        System.out.println(fst.read(5));
        System.out.println(snd.read(6));
    }
}
