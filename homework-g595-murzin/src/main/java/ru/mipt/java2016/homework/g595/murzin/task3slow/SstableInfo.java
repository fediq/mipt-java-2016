package ru.mipt.java2016.homework.g595.murzin.task3slow;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by dima on 15.11.16.
 */
public class SstableInfo<Key, Value> {
    public final File file;
    public final int fileIndex;
    private BufferedRandomAccessFile bufferedRandomAccessFile;

    public final int size;
    public final Key[] keys;
    public final long[] valuesOffsets;

    public SstableInfo(int fileIndex, File file, KeyWrapper<Key, Value>[] wrappers) {
        this.fileIndex = fileIndex;
        this.file = file;
        size = wrappers.length;
        keys = (Key[]) new Object[size];
        valuesOffsets = new long[size];
        for (int i = 0; i < size; i++) {
            keys[i] = wrappers[i].key;
            valuesOffsets[i] = wrappers[i].getOffsetInFile();
        }
    }

    public BufferedRandomAccessFile getBufferedRandomAccessFile() throws FileNotFoundException {
        if (bufferedRandomAccessFile == null) {
            bufferedRandomAccessFile = new BufferedRandomAccessFile(file);
        }
        return bufferedRandomAccessFile;
    }
}
