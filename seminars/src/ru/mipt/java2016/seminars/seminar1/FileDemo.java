package ru.mipt.java2016.seminars.seminar1;

import java.io.File;

/**
 * Работа с File
 */
public class FileDemo {
    public static void main(String[] args) {
        String filePath = FileDemo.class.getSimpleName() + ".java";
        File file = new File(filePath);
        if (file.exists()) {
            File parentFile = file.getAbsoluteFile().getParentFile();
            listRecursive(parentFile);

            // Использование File.separator
            System.out.println("My file: " + parentFile.getAbsolutePath() +
                    File.separator + filePath);
        }
    }

    private static void listRecursive(File dir) {
        if (dir.isDirectory()) {
            File[] items = dir.listFiles();
            for (File item : items) {
                System.out.println(item.getAbsoluteFile());
                if (item.isDirectory()) {
                    listRecursive(item);
                }
            }
        }
    }
}
