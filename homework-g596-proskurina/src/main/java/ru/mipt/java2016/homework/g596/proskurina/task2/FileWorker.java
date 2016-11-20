package ru.mipt.java2016.homework.g596.proskurina.task2;

import java.io.*;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class FileWorker {

    private static void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
    }

    public static String read(String fileName) throws FileNotFoundException {

        StringBuffer inputData = new StringBuffer();
        exists(fileName);
        File file = new File(fileName);

        try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            String s;
            while ((s = in.readLine()) != null) {
                inputData.append(s);
                inputData.append("\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputData.toString();
    }

    public static void write(String fileName, String text) {

        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
