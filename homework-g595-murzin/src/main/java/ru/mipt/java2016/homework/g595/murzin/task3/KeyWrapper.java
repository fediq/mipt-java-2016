package ru.mipt.java2016.homework.g595.murzin.task3;

/**
 * Created by dima on 05.11.16.
 */
public class KeyWrapper<Key, Value> {
    public final Key key;
    private int fileIndex = -1;
    private long offsetInFile = -1;

    private Value value; // Кеш для последних N операций write
    private int indexInNewEntries = -1;

    public KeyWrapper(Key key, Value value, int indexInNewEntries) {
        this.key = key;
        this.value = value;
        this.indexInNewEntries = indexInNewEntries;
    }

    public KeyWrapper(Key key, int fileIndex, long offsetInFile) {
        this.key = key;
        this.fileIndex = fileIndex;
        this.offsetInFile = offsetInFile;
    }

    @Override
    public String toString() {
        return "KeyWrapper{" +
                "fileIndex=" + fileIndex +
                ", offsetInFile=" + offsetInFile +
                '}';
    }

    public long getOffsetInFile() {
        return offsetInFile;
    }

    public void setOffsetInFile(long offsetInFile) {
        this.offsetInFile = offsetInFile;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public void setIndexInNewEntries(int indexInNewEntries) {
        this.indexInNewEntries = indexInNewEntries;
    }

    public int getIndexInNewEntries() {
        return indexInNewEntries;
    }
}
