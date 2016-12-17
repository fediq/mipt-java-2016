package ru.mipt.java2016.homework.g595.zueva.task2.task3;
import java.io.IOException;
public class testing {

    public static void main(String[] args) {
        OptKVStorage<Integer, Integer> fst =
                null;
        try {
            fst = new OptKVStorage("/home/king", new OptIntegerSerializer(), new OptIntegerSerializer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fst.write(5, 5);
        OptKVStorage snd =
                null;
        try {
            snd = new OptKVStorage("/something", new OptIntegerSerializer(), new OptIntegerSerializer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        snd.write(6, 6);
        snd.write(5, 6);
        System.out.println(fst.read(5));
        System.out.println(snd.read(6));
    }
}
