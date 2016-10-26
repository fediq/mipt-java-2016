package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.StringSerializer;

/**
 * Created by Anatoly on 26.10.2016.
 */
public class mytester {
    public static void main(String[] args) {
        KVStorageImpl<String, String> st = new KVStorageImpl<String, String>("", new StringSerializer(),
                new StringSerializer(), "string", "string");

        st.write("kuiibjllllllli", "pjdtjdtjdja");

    }
}
