package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Howl on 30.10.2016.
 */
public class DoubleParser implements ItemParser<Double> {

    @Override
    public void serialize(Double arg, FileOutputStream out) throws IOException {
        long tmp = Double.doubleToLongBits(arg);
        for (long i = 0; i < 8; ++i) {
            out.write((byte) ((tmp >> ((7 - i) * 8)) & 0xFFL));
        }
    }

    @Override
    public Double deserialize(FileInputStream in) throws IOException {
        long tmp = 0;
        for (int i = 0; i < 8; ++i) {
            tmp = (tmp << 8) + (byte) in.read();
        }

        return Double.longBitsToDouble(tmp);
    }
}
