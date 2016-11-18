package ru.mipt.java2016.homework.g595.murzin.task3fast;

/**
 * Created by dima on 18.11.16.
 */
public class KeyWrapper {
    private int valueLength;
    private long offsetInFile;

    public KeyWrapper(int valueLength, long offsetInFile) {
        this.valueLength = valueLength;
        this.offsetInFile = offsetInFile;
    }

    public int getValueLength() {
        return valueLength;
    }

    public void setValueLength(int valueLength) {
        this.valueLength = valueLength;
    }

    public long getOffsetInFile() {
        return offsetInFile;
    }

    public void setOffsetInFile(long offsetInFile) {
        this.offsetInFile = offsetInFile;
    }
}
