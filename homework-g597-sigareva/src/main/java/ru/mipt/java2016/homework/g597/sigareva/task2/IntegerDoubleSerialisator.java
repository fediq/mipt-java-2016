package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;

import java.io.IOException;

/**
 * Created by 1 on 30.10.2016.
 */
public class IntegerDoubleSerialisator extends ObjectSerialisator {

    IntegerDoubleSerialisator(String path_) {
        super(path_);
    }

    @Override
    void write(Object key, Object value) {
        outputStream.print(key);
        outputStream.print(":");
        outputStream.print(value);
        outputStream.print("\n");
    }

    @Override
    Pair read() throws IOException {
        String input = inputStream.readLine();
        if (input == null){
            throw new IOException("EOF");
        }
        else {
            int border = input.indexOf(":");
            Integer key = Integer.parseInt(input.substring(0, border));
            Double value = Double.parseDouble(input.substring(border + 1, input.length()));
            return new Pair(key, value);
        }
    }
}
