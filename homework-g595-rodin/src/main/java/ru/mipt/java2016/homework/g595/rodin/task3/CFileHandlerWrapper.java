package ru.mipt.java2016.homework.g595.rodin.task3;


/**
 * Created by Dmitry on 17.11.16.
 */
public class CFileHandlerWrapper implements Comparable<CFileHandlerWrapper> {
    private CFileHandler file;
    private long creationEpoch;
    private final String validationString;

    CFileHandlerWrapper(CFileHandler file, long creationEpoch
            , String validationString) {
        this.file = file;
        this.creationEpoch = creationEpoch;
        this.validationString = validationString;

        if(file.exists()) {
            validate();
        } else {
            createValidationBlock();
        }
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

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public void validate() {
        //#TODO realisation
    }

    public void createValidationBlock() {
        file.reposition(0);

    }

}
