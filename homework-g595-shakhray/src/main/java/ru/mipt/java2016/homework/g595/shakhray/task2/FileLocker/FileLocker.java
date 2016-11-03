package ru.mipt.java2016.homework.g595.shakhray.task2.FileLocker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Vlad on 01/11/2016.
 */
public class FileLocker {

    private final String lockFilename = ".lock";

    private static final FileLocker LOCKER = new FileLocker();

    private FileLocker() {
    }

    public static FileLocker getFileLocker() {
        return LOCKER;
    }

    public boolean checkLock(String path) throws FileNotFoundException {
        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new FileNotFoundException("Directory is invalid");
        }
        String absoluteLockFilename = path + File.separator + lockFilename;
        File lock = new File(absoluteLockFilename);
        return lock.exists();
    }

    public void setLock(String path) throws IllegalStateException, IOException {
        if (checkLock(path)) {
            throw new IllegalStateException("Storage is locked.");
        }
        String absoluteLockFilename = path + File.separator + lockFilename;
        File f = new File(absoluteLockFilename);
        RandomAccessFile lock = new RandomAccessFile(f, "rw");
        lock.close();
    }

    public void unsetLock(String path) throws IllegalStateException, IOException {
        if (!checkLock(path)) {
            throw new IllegalStateException("No lock is set.");
        }
        String absoluteLockFilename = path + File.separator + lockFilename;
        Files.delete(Paths.get(absoluteLockFilename));
    }
}
