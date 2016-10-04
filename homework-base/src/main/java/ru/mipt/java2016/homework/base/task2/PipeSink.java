package ru.mipt.java2016.homework.base.task2;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * Потоковый приемник.
 * Принимает итератор и с каждым его элементом проводит какое-то действие. Например, сохраняет в базу.
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */
public interface PipeSink<Value> extends Closeable {
    void sink(Iterator<Value> values) throws IOException;
}
