package ru.mipt.java2016.homework.g595.murzin.task3slow;

/**
 * Created by dima on 05.11.16.
 */
public class KeyWrapper<Key, Value> {
    public final Key key;
    private int tableIndex = -1;
    private long offsetInFile = -1;

    private Value value; // Кеш для последних N операций write
    private int indexInNewEntries = -1;

    public KeyWrapper(Key key, Value value, int indexInNewEntries) {
        this.key = key;
        this.value = value;
        this.indexInNewEntries = indexInNewEntries;
    }

    public KeyWrapper(Key key, int tableIndex, long offsetInFile) {
        this.key = key;
        this.tableIndex = tableIndex;
        this.offsetInFile = offsetInFile;
    }

    @Override
    public String toString() {
        return "KeyWrapper{" +
                "fileIndex=" + tableIndex +
                ", offsetInFile=" + offsetInFile +
                '}';
    }

    public long getOffsetInFile() {
        return offsetInFile;
    }

    public void setOffsetInFile(long offsetInFile) {
        this.offsetInFile = offsetInFile;
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(int fileIndex) {
        this.tableIndex = fileIndex;
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
