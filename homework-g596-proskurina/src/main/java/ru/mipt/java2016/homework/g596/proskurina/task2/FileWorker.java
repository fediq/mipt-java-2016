package ru.mipt.java2016.homework.g596.proskurina.task2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class FileWorker {
    public static void write(String fileName, String text) {

        File file = new File(fileName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
