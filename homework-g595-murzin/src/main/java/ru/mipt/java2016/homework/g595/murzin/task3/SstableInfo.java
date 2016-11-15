package ru.mipt.java2016.homework.g595.murzin.task3;

/**
 * Created by dima on 15.11.16.
 */
public class SstableInfo<Key, Value> {
    public final int length;
    public final Key[] keys;
    public final long[] valuesOffsets;

    public SstableInfo(KeyWrapper<Key, Value>[] wrappers) {
        length = wrappers.length;
        keys = (Key[]) new Object[length];
        valuesOffsets = new long[length];
        for (int i = 0; i < length; i++) {
            keys[i] = wrappers[i].key;
            valuesOffsets[i] = wrappers[i].getOffsetInFile();
        }
    }
}
