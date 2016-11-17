package ru.mipt.java2016.homework.g594.shevkunov.task3;

/**
 * Pointer to files
 * Created by shevkunov on 14.11.16.
 */
class LazyMergedKeyValueStorageFileNode {
    private long file;
    private long offset;

    LazyMergedKeyValueStorageFileNode(long file, long offset) {
        set(file, offset);
    }

    void set(long fileIndex, long offsetValue) {
        this.file = fileIndex;
        this.offset = offsetValue;
    }

    long getFile() {
        return file;
    }

    long getOffset() {
        return offset;
    }
}
