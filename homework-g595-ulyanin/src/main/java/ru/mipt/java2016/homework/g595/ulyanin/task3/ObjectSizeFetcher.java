package ru.mipt.java2016.homework.g595.ulyanin.task3;

import java.lang.instrument.Instrumentation;

/**
 * @author ulyanin
 * @since 16.11.16.
 */
public class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation instance) {
        instrumentation = instance;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}
