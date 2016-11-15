package ru.mipt.java2016.homework.g595.murzin.task3;

/**
 * Created by dima on 05.11.16.
 */
public class KeyWrapper<Key, Value> {
    public final Key key;
    public int fileIndex = -1;
    public long offsetInFile = -1;

    public Value value; // Кеш для последних N операций write
    public int indexInNewEntries = -1;

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
}
