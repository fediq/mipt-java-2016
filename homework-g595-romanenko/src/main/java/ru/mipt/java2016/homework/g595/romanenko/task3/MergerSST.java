package ru.mipt.java2016.homework.g595.romanenko.task3;

import ru.mipt.java2016.homework.g595.romanenko.task2.Producer;
import ru.mipt.java2016.homework.g595.romanenko.task2.SSTable;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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

    public SSTable<K, V> merge(String path, SSTable<K, V> newer, SSTable<K, V> older,
                               FileDigitalSignature fileDigitalSignature) throws IOException {
        SSTable<K, V> result = new SSTable<>(
                path,
                newer.getKeySerializationStrategy(),
                newer.getValueSerializationStrategy(),
                fileDigitalSignature);

        List<K> newerKeys = getKeys(newer.readKeys());
        List<K> olderKeys = getKeys(older.readKeys());
        List<K> resultKeys = new ArrayList<>();

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
        while (olderKeysPos < olderKeys.size()) {
            resultKeys.add(olderKeys.get(olderKeysPos));
            olderKeysPos += 1;
        }

        /* //Check for correct merge
        for (int i = 1; i < resultKeys.size(); i++)
            if (keyComparator.compare(resultKeys.get(i - 1), resultKeys.get(i)) >= 0) {
                assert false;
            }
        */

        result.rewrite(new Producer<K, V>() {
            @Override
            public List<K> keyList() {
                return resultKeys;
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
                return resultKeys.size();
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

    public Comparator<? super K> getComparator() {
        return keyComparator;
    }
}
