package ru.mipt.java2016.homework.g594.kozlov.task2;

import java.io.*;

/**
 * Created by Anatoly on 26.10.2016.
 */
public class FileWorker {

    private final File file;

    private final String fileName;

    public FileWorker(String fileName) {
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

    public void write(String text) {

        try {
            exists();
            FileWriter out = new FileWriter(file.getAbsoluteFile());
            try {
                out.write(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        exists();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
