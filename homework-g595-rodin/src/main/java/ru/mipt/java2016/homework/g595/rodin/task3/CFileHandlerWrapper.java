package ru.mipt.java2016.homework.g595.rodin.task3;

/**
 * Created by Dmitry on 17.11.16.
 */
public class CFileHandlerWrapper implements Comparable<CFileHandlerWrapper> {
    private CFileHandler file;
    private long creationEpoch;

    CFileHandlerWrapper(CFileHandler file, long creationEpoch) {
        this.file = file;
        this.creationEpoch = creationEpoch;
    }

    @Override
    public int compareTo(CFileHandlerWrapper other) {
        return Long.compare(this.creationEpoch, other.creationEpoch);
    }

    public String getFileName() {
        return file.getFileName();
    }

    public CFileHandler getFile() {
        return file;
    }
}
