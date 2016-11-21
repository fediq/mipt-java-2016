package ru.mipt.java2016.homework.g596.ivanova.task3;

import java.lang.instrument.Instrumentation;

/**
 * @author julia
 * @since 20.11.16.
 */
public class ObjectSize {
    private static Instrumentation instrumentation;

    public static void premain(final String args, final Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(final Object o) {
        if (instrumentation == null) {
            throw new IllegalStateException("Agent not initialised");
        }
        return instrumentation.getObjectSize(o);
    }
}
