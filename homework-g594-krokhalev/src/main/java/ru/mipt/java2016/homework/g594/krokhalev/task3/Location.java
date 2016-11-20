package ru.mipt.java2016.homework.g594.krokhalev.task3;

public class Location {
    private long mPos;
    private int mLen;

    public Location(long pos, int len) {
        mPos = pos;
        mLen = len;
    }

    long getPos() {
        return mPos;
    }

    int getLen() {
        return mLen;
    }
}