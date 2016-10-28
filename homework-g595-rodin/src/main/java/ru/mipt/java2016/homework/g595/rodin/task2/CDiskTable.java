package ru.mipt.java2016.homework.g595.rodin.task2;

import java.io.*;

/**
 * Created by Dmitry on 26.10.16.
 */

public class CDiskTable {

    private final File file;

    private final String filePath;

    public CDiskTable(String filePath) {
        this.file = new File(filePath);
        this.filePath = filePath;
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
        StringBuilder stringBuilder = new StringBuilder();
        exists();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String s;
                while ((s = reader.readLine()) != null) {
                    stringBuilder.append(s);
                    stringBuilder.append("\n");
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}
