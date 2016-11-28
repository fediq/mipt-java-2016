package ru.mipt.java2016.seminars.seminar1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Пример работы с FileInputStream/FileOutputStream.
 */
public class FileStreamsDemo {
    public static void main(String[] args) {
        // Чтение из файла
        try (InputStream inputStream = new FileInputStream("FileStreamsDemo.java")) {
            // Узнать размер файла:
            int fileSize = inputStream.available();
            System.out.println("File size: " + fileSize);

            // Чтение из файла:
            // 1. Побайтово
            for (int i = 0; i < fileSize / 4; ++i) {
                System.out.print((char) inputStream.read());
            }
            // 2. В массив
            byte[] buffer = new byte[fileSize / 4];
            if (inputStream.read(buffer) != fileSize / 4) {
                System.err.println("Couldn't read " + fileSize / 4 + " bytes");
                return;
            }
            System.out.println(new String(buffer, 0, fileSize / 4));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        }

        String inputString = "package ru.mipt.java2016.seminars.seminar1;\n"
                + "\n"
                + "import java.io.FileInputStream;\n"
                + "import java.io.FileOutputStream;\n"
                + "import java.o.IOException;";

        // Запись в файл
        try (OutputStream outputStream = new FileOutputStream("OutputFile.txt")) {
            outputStream.write(inputString.getBytes());
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        }
    }
}
