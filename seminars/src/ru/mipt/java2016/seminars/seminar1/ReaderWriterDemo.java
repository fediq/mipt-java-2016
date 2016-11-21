package ru.mipt.java2016.seminars.seminar1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Пример работы с символьными потоками
 */
public class ReaderWriterDemo {
    public static void main(String[] args) {
        String strFilename = "out.txt";
        String message = "Hello, world!\nHello, world again!\n";

        try (BufferedWriter out = new BufferedWriter(new FileWriter(strFilename))) {
            out.write(message);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (BufferedReader in = new BufferedReader(new FileReader(strFilename))) {
            String inLine;
            while ((inLine = in.readLine()) != null) {
                System.out.println(inLine);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
