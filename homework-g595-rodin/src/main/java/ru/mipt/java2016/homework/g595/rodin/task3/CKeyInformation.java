package ru.mipt.java2016.homework.g595.rodin.task3;


public class CKeyInformation {

    private long offset = -1;
    private boolean isPresent = false;
    private String filename = "";

    public CKeyInformation(Long offset,String filename, Boolean isPresent){
        this.offset = offset;
        this.isPresent = isPresent;
        this.filename = filename;
    }

    public Boolean getPresence(){
        return isPresent;
    }

    public Long getOffset(){
        return offset;
    }

    public String getFilename(){
        return filename;
    }

    public void setDeleted(){
        isPresent = false;
    }
}
