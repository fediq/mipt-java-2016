package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.util.List;
import java.util.Set;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2
 *
 * @author Ilya I. Romanenko
 * @since 31.10.16
 **/

public interface Producer<Key, Value> {

    Value get(Key key);

    int size();

    List<Key> keyList();

    Set<Key> keySet();
}