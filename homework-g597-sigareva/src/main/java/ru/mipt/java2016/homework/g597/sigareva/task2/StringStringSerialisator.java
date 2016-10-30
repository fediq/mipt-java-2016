package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;

import java.io.IOException;

/**
 * Created by 1 on 31.10.2016.
 */
public class StringStringSerialisator extends ObjectSerialisator<String, String> {

    StringStringSerialisator(String path_) {
        super(path_);
    }

    @Override
    void write(String key, String value) {
        outputStream.print(key);
        outputStream.print(":");
        outputStream.print(value);
        outputStream.print("\n");
    }

    @Override
    Pair<String, String> read() throws IOException {
        String input = inputStream.readLine();
        System.out.println(input);
        if (input == null){
            throw new IOException("EOF");
        }
        else {
            int border = input.indexOf(":");
            String key = input.substring(0, border);
            String value = input.substring(border + 1, input.length());
            return new Pair(key, value);
        }
    }
}

