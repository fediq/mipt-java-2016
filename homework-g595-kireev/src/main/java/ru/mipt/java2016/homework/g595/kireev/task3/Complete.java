package ru.mipt.java2016.homework.g595.kireev.task3;

import java.io.IOException;

/**
 * Created by sun on 17.12.16.
 */
public class Complete {

    public static void main(String[] args) throws IOException {

        MyKeyValueStorage64<String, String> store =
                new MyKeyValueStorage64<String, String>("String", "String", "~/java");
  //      store.write("Karim", "Kireev");
        System.out.println(store.read("Karim"));
        store.close();

    }

}
