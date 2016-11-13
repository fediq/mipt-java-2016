package ru.mipt.java2016.homework.g595.murzin;

import ru.mipt.java2016.homework.g595.murzin.task3.LSMStorage;
import ru.mipt.java2016.homework.g595.murzin.task3.SerializationStrategy;

import java.nio.file.Files;
import java.util.stream.IntStream;

/**
 * Created by dima on 06.11.16.
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        IntStream.range(0, 20).forEach(System.out::println);
        String path = Files.createTempDirectory("temp").toFile().getAbsolutePath();
        LSMStorage<Integer, Integer> storage = getIntegerIntegerLSMStorage(path);

        int n = 1000;
        IntStream.range(0, n).forEach(x -> storage.write(x, x));
        storage.close();

        LSMStorage<Integer, Integer> storage2 = getIntegerIntegerLSMStorage(path);
        IntStream.range(0, n).filter(x -> x != storage2.read(x)).mapToObj(x -> "! " + x + " " + storage2.read(x)).forEach(System.out::println);
//        IntStream.range(0, n).mapToObj(x -> x + " " + storage.read(x)).forEach(System.out::println);
    }

    private static LSMStorage<Integer, Integer> getIntegerIntegerLSMStorage(String path) {
        return new LSMStorage<>(path,
                SerializationStrategy.FOR_INTEGER,
                SerializationStrategy.FOR_INTEGER,
                Integer::compareTo);
    }
}
