package ru.mipt.java2016.homework.g594.pyrkin.task2;

import java.io.File;

/**
 * Created by randan on 10/30/16.
 */
public class FileWorker {

    private final String directoryPath;
    private final String fileName;

    private final File file;

    public FileWorker(String directoryPath, String fileName) {
        this.directoryPath = directoryPath;
        this.fileName = fileName;
        file = new File(directoryPath, fileName);
    }


}
