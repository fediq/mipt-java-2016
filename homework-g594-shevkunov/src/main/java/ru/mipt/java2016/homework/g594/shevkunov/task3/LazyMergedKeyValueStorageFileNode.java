package ru.mipt.java2016.homework.g594.shevkunov.task3;

/**
 * Pointer to files
 * Created by shevkunov on 14.11.16.
 */
public class LazyMergedKeyValueStorageFileNode {
    private long file;
    private long offset;

    public LazyMergedKeyValueStorageFileNode(long file, long offset) {
        set(file, offset);
    }

    public void set(long fileIndex, long offsetValue) {
        this.file = fileIndex;
        this.offset = offsetValue;
    }

    public long getFile() {
        return file;
    }

    public long getOffset() {
        return offset;
    }
}
