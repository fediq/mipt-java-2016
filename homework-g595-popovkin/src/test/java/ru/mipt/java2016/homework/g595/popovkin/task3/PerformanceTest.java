package ru.mipt.java2016.homework.g595.popovkin.task3;
import java.io.*;
import java.time.Clock;

/**
 * Created by Howl on 16.11.2016.
 */
public class PerformanceTest {
    public static void main(String []args) throws IOException {
        File file = new File("./test");
        file.createNewFile();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
        for (int i = 0; i < 100000000; ++i)
            out.write(213);
        out.close();
        System.out.println("Written");
        //Clock timer = Clock();
        //long tmp = timer.millis();
        //for (int i = 0)
    }
}
