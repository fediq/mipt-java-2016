package ru.mipt.java2016.homework.g595.tkachenko.task3;

/**
 * Created by Dmitry on 22/11/2016.
 */

public class KeyPosition {

    private final long fileNumber;
    private final long positionInFile;

    KeyPosition(long fileNumber, long positionInFile) {
        this.fileNumber = fileNumber;
        this.positionInFile = positionInFile;
    }

    public long getFileNumber() {
        return fileNumber;
    }

    public long getPositionInFile() {
        return positionInFile;
    }
}
