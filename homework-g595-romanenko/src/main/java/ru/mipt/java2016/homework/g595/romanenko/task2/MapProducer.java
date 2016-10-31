package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.util.Map;
import java.util.Set;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task2
 *
 * @author Ilya I. Romanenko
 * @since 31.10.16
 **/
public class MapProducer<Key, Value> implements Producer<Key, Value> {

    private final Map<Key, Value> map;

    public MapProducer(Map<Key, Value> keyValueMap) {
        map = keyValueMap;
    }

    @Override
    public Set<Key> keySet() {
        return map.keySet();
    }

    @Override
    public Value get(Key key) {
        return map.get(key);
    }

    @Override
    public int size() {
        return map.size();
    }
}
