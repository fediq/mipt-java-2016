package ru.mipt.java2016.homework.g595.topilskiy.task2;

/**
 * A Class storing KeyValueStorage Information
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class LazyByteKeyValueStorageInfo {
    /* The path to the directory of data storage */
    private final String pathToStorageDirectory;
    /* A String reflecting the name of the KeyType in KeyValueStorage */
    private final String keyTypeString;
    /* A String reflecting the name of the ValueType in KeyValueStorage */
    private final String valueTypeString;

    public LazyByteKeyValueStorageInfo(String pathToStorageDirectoryInit,
                                String keyTypeStringInit,
                                String valueTypeStringInit) {
        pathToStorageDirectory = pathToStorageDirectoryInit;
        keyTypeString = keyTypeStringInit;
        valueTypeString = valueTypeStringInit;
    }

    public String getPathToStorageDirectory() {
        return pathToStorageDirectory;
    }

    public String getKeyTypeString() {
        return keyTypeString;
    }

    public String getValueTypeString() {
        return valueTypeString;
    }
}
