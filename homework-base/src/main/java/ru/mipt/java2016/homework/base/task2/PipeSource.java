package ru.mipt.java2016.homework.base.task2;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * Потоковый источник.
 * Порождает итератор каких-то элементов. Например, читает из базы.
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */
public interface PipeSource<Value> extends Closeable {
    Iterator<Value> source() throws IOException;
}
