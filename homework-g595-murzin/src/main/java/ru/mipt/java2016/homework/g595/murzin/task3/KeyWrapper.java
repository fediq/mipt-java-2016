package ru.mipt.java2016.homework.g595.murzin.task3;

/**
 * Created by dima on 18.11.16.
 */
public class KeyWrapper {
    public final int valueLength;
    public final long offsetInFile;

    public KeyWrapper(int valueLength, long offsetInFile) {
        this.valueLength = valueLength;
        this.offsetInFile = offsetInFile;
    }
}
