package ru.mipt.java2016.homework.g596.kozlova.task3;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.DataInput;
import java.io.IOException;

public class MyDoubleSerialization implements MySerialization<Double> {
    @Override
    public String write(Double obj) throws IOException {
        return obj.toString();
    }

    @Override
    public Double read(DataInput input) throws IOException {
        return input.readDouble();
    }
}
