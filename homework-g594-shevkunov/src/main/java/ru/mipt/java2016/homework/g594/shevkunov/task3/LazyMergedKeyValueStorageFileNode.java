package ru.mipt.java2016.homework.g594.shevkunov.task3;

/**
 * Pointer to files
 * Created by shevkunov on 14.11.16.
 */
class LazyMergedKeyValueStorageFileNode {
    private int file;
    private long offset;

    LazyMergedKeyValueStorageFileNode(int file, long offset) {
        set(file, offset);
    }

    void set(int fileIndex, long offsetValue) {
        this.file = fileIndex;
        this.offset = offsetValue;
    }

    int getFile() {
        return file;
    }

    long getOffset() {
        return offset;
    }
}
