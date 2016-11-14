package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

public class TablesController<K, V> {
    private static final String SSTABLE_NAME = "SSTable";

    private File workDirectory;

    private LinkedList<File> ssTables  = new LinkedList<>();

    private int fileCount;

    TablesController(File workDirectory, Class<K> keyClass, Class<V> valueClass) {
        this.workDirectory = workDirectory;

        if (workDirectory.exists() && workDirectory.isDirectory()) {

            File ssTable = findSSTable();

            if (ssTable == null) {

                fileCount = 0;

            } else {

                ssTables.add(ssTable);

                fileCount = 1;
            }

        } else {
            throw new RuntimeException("Bad directory \"" + workDirectory.getAbsolutePath() + "\"");
        }

    }

    public int getCount() {
        return fileCount;
    }

    private boolean isFileExists(String fileName) {
        for (File item : workDirectory.listFiles()) {
            if (item.isFile() && item.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    private File findSSTable() {
        for (File item : workDirectory.listFiles()) {
            if (item.isFile() && item.getName().equals(getSSTableName())) {
                return item;
            }
        }
        return null;
    }

    private String getSSTableName() {
        return getSSTableName(0);
    }

    private String getSSTableName(int ind) {
        if (ind == 0) {
            return SSTABLE_NAME + ".db";
        } else {
            return SSTABLE_NAME + "_" + String.valueOf(ind) + ".db";
        }
    }


}
