package ru.mipt.java2016.homework.g595.romanenko.task3;

import ru.mipt.java2016.homework.g595.romanenko.task2.Producer;
import ru.mipt.java2016.homework.g595.romanenko.task2.SSTable;

import java.io.IOException;
import java.util.*;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task3
 *
 * @author Ilya I. Romanenko
 * @since 30.10.16
 **/
public class MergerSST<K, V> {

    private Comparator<K> keyComparator;

    public MergerSST(Comparator<K> keyComparator) {
        this.keyComparator = keyComparator;
    }

    public SSTable<K, V> merge(String path, SSTable<K, V> newer, SSTable<K, V> older) throws IOException {
        SSTable<K, V> result = new SSTable<>(
                path,
                newer.getKeySerializationStrategy(),
                newer.getValueSerializationStrategy());

        List<K> newerKeys = getKeys(newer.readKeys());
        List<K> olderKeys = getKeys(older.readKeys());
        List<K> resultKeys = new ArrayList<>();

        newerKeys.sort(keyComparator);
        olderKeys.sort(keyComparator);

        int olderKeysPos = 0;

        for (K newerKey : newerKeys) {
            while (olderKeysPos < olderKeys.size()) {
                int compareResult = keyComparator.compare(newerKey, olderKeys.get(olderKeysPos));
                if (compareResult > 0) {
                    resultKeys.add(olderKeys.get(olderKeysPos));
                    olderKeysPos++;
                } else {
                    if (compareResult == 0) { // remove old key
                        olderKeysPos++;
                    }
                    break;
                }
            }
            resultKeys.add(newerKey);
        }
        Set<K> keySet = new HashSet<>(resultKeys);
        result.rewrite(new Producer<K, V>() {
            @Override
            public Set<K> keySet() {
                return keySet;
            }

            @Override
            public V get(K k) {
                if (newer.exists(k)) {
                    return newer.getValue(k);
                }
                return older.getValue(k);
            }

            @Override
            public int size() {
                return keySet.size();
            }
        });

        return result;
    }

    private List<K> getKeys(Iterator<K> it) {
        ArrayList<K> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }
}
