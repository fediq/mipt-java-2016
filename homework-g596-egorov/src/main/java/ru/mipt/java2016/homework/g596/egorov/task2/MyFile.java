package ru.mipt.java2016.homework.g596.egorov.task2;

/**
 * Created by евгений on 30.10.2016.
 * Данный класс будет "облуживать файл"
 *
 */
import java.io.*;

public class MyFile {

    private final File file;

    private final String fileName;

    public MyFile(String fileN) {
        file = new File(fileN);
        fileName = fileN;
    }




   /* public boolean exists() throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        return true;
    }*/

    public String name() {
        return this.fileName;
    }




}