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

    public MyFile(String fileName) {
        this.file = new File(fileName);
        this.fileName = fileName;
    }

    public void createFile() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean exists() throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        return true;
    }

    public String name() {
        return this.fileName;
    }

    public void fill(String cheker) {
        try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(this.fileName))) {
            wr.writeUTF(cheker);
        } catch (IOException e) {
            throw new IllegalStateException("Smth goes wrong: Can't write to file!");
        }
    }
}