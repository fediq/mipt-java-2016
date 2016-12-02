package ru.mipt.java2016.seminars.seminar1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Пример работы с FileInputStream/FileOutputStream без try-with-resources
 */
public class FileStreamsOldErrorHandlingDemo {
    public static void main(String[] args) {
        // Чтение из файла
        InputStream inputStream = null;
        try {
            String path = FileStreamsOldErrorHandlingDemo.class.getSimpleName() + ".java";
            inputStream = new FileInputStream(path);
            // Узнать размер файла:
            int fileSize = inputStream.available();
            System.out.println("File size: " + fileSize);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
