package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.util.*;
import java.io.*;

/**
 * Created by Howl on 29.10.2016.
 */
public class MySerializer<V, K> {
    private ArrayList<Byte> additional_bytes = new ArrayList<>();
    private int step_ = 0;

    public MySerializer() {  }

    public HashMap<K, V> parce(FileInputStream in) {
        HashMap<K, V> map = new HashMap<>();



        return null;
    }

    /*
    private <T> void parce(T type_) {

    }

    public void push(int byte_) {
        additional_bytes.add((byte)byte_);
        if (step_ == 0) {
            V tmp = null;
            parce(tmp);
        } else {
            K tmp = null;
            parce(tmp);
        }
    }
    */
}
