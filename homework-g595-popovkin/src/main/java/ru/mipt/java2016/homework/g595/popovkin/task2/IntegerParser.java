package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Howl on 30.10.2016.
 */

public class IntegerParser implements ItemParser<Integer> {

    @Override
    public void serialize(Integer argTmp, FileOutputStream out) throws IOException {
        int arg = argTmp;
        for (int i = 0; i < 4; ++i) {
            out.write((arg >> ((3 - i) * 8)) & 0xFF);
        }
    }

    @Override
    public Integer deserialize(FileInputStream in) throws IOException {
        int ans = 0;
        for (int i = 0; i < 4; ++i) {
            Integer tmp = (int) in.read();
            ans = (ans << 8) + tmp;
        }
        return ans;
    }
}