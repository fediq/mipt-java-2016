package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.util.Set;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2
 *
 * @author Ilya I. Romanenko
 * @since 31.10.16
 **/

public interface Producer<Key, Value> {
    Set<Key> keySet();

    Value get(Key key);

    int size();
}