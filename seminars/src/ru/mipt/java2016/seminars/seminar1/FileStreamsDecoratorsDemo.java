package ru.mipt.java2016.seminars.seminar1;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Пример работы с обертками над FileInputStream/FileOutputStream.
 */
public class FileStreamsDecoratorsDemo {
    public static void main(String[] args) {
        // Оборачиваем в BufferedInputStream
        String path = FileStreamsDecoratorsDemo.class.getSimpleName() + ".java";
        try (InputStream inputStream =
                     new BufferedInputStream(new FileInputStream(path))) {
            int fileSize = inputStream.available();

            byte[] buffer = new byte[fileSize / 4];
            if (inputStream.read(buffer) != fileSize / 4) {
                System.err.println("Couldn't read " + fileSize / 4 + " bytes");
                return;
            }
            System.out.println(new String(buffer, 0, fileSize / 4));
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        }

        // Запись в файл
        try (DataOutputStream outputStream =
                     new DataOutputStream(new FileOutputStream("PODTypes.txt"))) {
            outputStream.writeInt(15);
            outputStream.writeDouble(15.5);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        }

        // Чтение записанного
        // Можно оборачивать сразу несколькими декораторами
        try (DataInputStream inputStream =
                     new DataInputStream(
                             new BufferedInputStream(
                                     new FileInputStream("PODTypes.txt")))) {
            System.out.println(inputStream.readInt());
            System.out.println(inputStream.readDouble());
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        }
    }
}
