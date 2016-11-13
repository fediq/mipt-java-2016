package ru.mipt.java2016.homework.g595.murzin.task3;

/**
 * Created by dima on 13.11.16.
 */
class KeyInfo<K> {
    public final K key;
    public final long offsetInFile;

    public KeyInfo(K key, long offsetInFile) {
        this.key = key;
        this.offsetInFile = offsetInFile;
    }

    @Override
    public String toString() {
        return "KeyInfo{" +
                "key=" + key +
                ", offsetInFile=" + offsetInFile +
                '}';
    }
}
