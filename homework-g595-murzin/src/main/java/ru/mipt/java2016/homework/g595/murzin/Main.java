package ru.mipt.java2016.homework.g595.murzin;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by dima on 06.11.16.
 */
public class Main<T> {
    public Main() {
        T[] t = (T[]) Stream.of("1", "3", "2").toArray();
        System.out.println(Arrays.toString(t));
    }

    public static void main(String[] args) throws Exception {
        new Main<Integer>();

        /*File temp = Files.createTempFile("test", ".txt").toFile();
        try (PrintWriter output = new PrintWriter(temp)) {
            output.println("Hello!");
        }
        Files.move(temp.toPath(), new File("copy.txt").toPath());*/
    }
}
