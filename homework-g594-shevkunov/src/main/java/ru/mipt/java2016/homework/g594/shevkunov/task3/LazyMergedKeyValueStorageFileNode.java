package ru.mipt.java2016.homework.g594.shevkunov.task3;

/**
 * Created by shevkunov on 14.11.16.
 */
public class LazyMergedKeyValueStorageFileNode {
    public final int file;
    public final long offset;

    public LazyMergedKeyValueStorageFileNode(int file, long offset) {
        this.file = file;
        this.offset = offset;
    }
}
