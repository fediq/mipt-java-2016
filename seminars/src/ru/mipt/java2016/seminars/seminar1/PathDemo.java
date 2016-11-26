package ru.mipt.java2016.seminars.seminar1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Работа с Path
 */
public class PathDemo {
    public static void main(String[] args) {
        Path path = Paths.get(PathDemo.class.getSimpleName() + ".java");
        System.out.println(path.toAbsolutePath());

        // Получение файла
        File file = path.toFile();

        // Работа с методами Files
        System.out.println("Exists: " + Files.exists(path));
        System.out.println("Is file: " + Files.isRegularFile(path));

        // Создание файлов
        try {
            Files.createDirectory(Paths.get("D:\\temp"));
            Files.createFile(Paths.get("D:\\temp\\temp.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
