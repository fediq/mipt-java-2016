package ru.mipt.java2016.homework.g594.stepanov.task2;

import javafx.util.Pair;

import java.io.*;

public class IntegerDoubleSerializator extends ObjectSerializator<Integer, Double> {

    public IntegerDoubleSerializator(String directory) throws IOException {
        super(directory);
    }

    @Override
    public void write(Integer key, Double value) throws IOException {
        Pair p = new Pair(key, value);
        currentHash += p.hashCode();
        StringBuilder sb = new StringBuilder("");
        sb.append(start);
        sb.append(key);
        sb.append(separator);
        sb.append(value);
        sb.append(finish);
        sb.append("\n");
        outputStream.print(sb.toString());
    }

    @Override
    public Pair<Integer, Double> read() throws IOException {
        String s = inputStream.readLine();
        if (s == null) {
            throw new IOException("File end");
        }
        int pos = s.indexOf(separator);
        if (!s.startsWith(start) || !s.endsWith(finish) || pos == -1) {
            throw new IOException("Invalid string in input file");
        }
        Integer key = Integer.parseInt(s.substring(1, pos));
        Double value = Double.parseDouble(s.substring(pos + 1, s.length() - 1));
        return new Pair(key, value);
    }

}
