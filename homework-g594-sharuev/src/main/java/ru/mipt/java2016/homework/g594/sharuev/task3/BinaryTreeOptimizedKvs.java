package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;

public class BinaryTreeOptimizedKvs<K, V> extends OptimizedKvs<K, V> {

    public BinaryTreeOptimizedKvs(String path, SerializationStrategy<K> keySerializationStrategy,
                                  SerializationStrategy<V> valueSerializationStrategy,
                                  Comparator<K> comparator) throws KVSException {
        super(path, keySerializationStrategy, valueSerializationStrategy, comparator);
    }

    /**
     * Смерживание двух частей в одну.
     * Берутся две части из начала дека, мержатся и итоговая часть кладётся в начало дека.
     * Мержатся они при помощи временного файла, который в конце переименовывается в имя первого из сливавшихся файлов.
     * Сложность O(Nlog(N))
     *
     * @throws IOException
     */
    @Override
    protected void mergeFiles() throws IOException {
        assert parts.size() >= 2;

        OptimizedKvs.Part bigPart = parts.getFirst();
        parts.pollFirst();

        while (parts.size() > 1) {
            Deque<Part> newParts = new ArrayDeque<>();
            // 1 и 2 в хронологическом порядке
            while (parts.size() > 1) {
                newParts.addFirst(mergeTwoLastParts());
            }
            if (parts.size() > 0) {
                newParts.addFirst(parts.getFirst());
            }
            parts = newParts;
        }

        parts.addFirst(bigPart);
        parts.addFirst(mergeTwoLastParts());

        indexTable.clear();
        for (int i = 0; i < parts.getFirst().keys.size(); ++i) {
            indexTable.put(parts.getFirst().keys.get(i),
                    new OptimizedKvs.Address(parts.getFirst(), parts.getFirst().offsets.get(i)));
        }
    }

    private Part mergeTwoLastParts() throws IOException {
        Part part2 = parts.getLast();
        parts.pollLast();
        Part part1 = parts.getLast();
        parts.pollLast();

        File tempFile = Paths.get(path, dbName + "Temp" + Consts.STORAGE_PART_SUFF).toFile();
        if (!tempFile.createNewFile()) {
            throw new KVSException("Temp file already exists");
        }

        Part newPart = new Part(new RandomAccessFile(tempFile, "rw"), tempFile);

        DataOutputStream out = bdosFromRaf(newPart.raf, Consts.BUFFER_SIZE);
        part1.raf.seek(0);
        part2.raf.seek(0);
        DataInputStream dis1 = bdisFromRaf(part1.raf, Consts.BUFFER_SIZE);
        DataInputStream dis2 = bdisFromRaf(part2.raf, Consts.BUFFER_SIZE);

        K entry1;
        K entry2;
        Iterator<K> it1 = part1.keys.iterator();
        Iterator<K> it2 = part2.keys.iterator();
        try {
            entry1 = it1.hasNext() ? it1.next() : null;
            entry2 = it2.hasNext() ? it2.next() : null;
            while (entry1 != null && entry2 != null) {
                if (!indexTable.containsKey(entry1)) {
                    entry1 = it1.hasNext() ? it1.next() : null;
                    valueSerializationStrategy.deserializeFromStream(dis1);
                    continue;
                }
                if (!indexTable.containsKey(entry2)) {
                    entry2 = it2.hasNext() ? it2.next() : null;
                    valueSerializationStrategy.deserializeFromStream(dis2);
                    continue;
                }
                if (comparator.compare(entry1, entry2) <= 0) {
                    newPart.keys.add(entry1);
                    newPart.offsets.add(out.size());
                    valueSerializationStrategy.serializeToStream(
                            valueSerializationStrategy.deserializeFromStream(dis1), out);
                    entry1 = it1.hasNext() ? it1.next() : null;
                } else { // if <=, поэтому из равных будет записан последний
                    newPart.keys.add(entry2);
                    newPart.offsets.add(out.size());
                    valueSerializationStrategy.serializeToStream(
                            valueSerializationStrategy.deserializeFromStream(dis2), out);
                    entry2 = it2.hasNext() ? it2.next() : null;
                }
            }
            while (entry1 != null) {
                if (indexTable.containsKey(entry1)) {
                    newPart.keys.add(entry1);
                    newPart.offsets.add(out.size());
                    valueSerializationStrategy.serializeToStream(
                            valueSerializationStrategy.deserializeFromStream(dis1), out);
                } else {
                    valueSerializationStrategy.deserializeFromStream(dis1);
                }
                entry1 = it1.hasNext() ? it1.next() : null;
            }
            while (entry2 != null) {
                if (indexTable.containsKey(entry2)) {
                    newPart.keys.add(entry2);
                    newPart.offsets.add(out.size());
                    valueSerializationStrategy.serializeToStream(
                            valueSerializationStrategy.deserializeFromStream(dis2), out);
                } else {
                    valueSerializationStrategy.deserializeFromStream(dis2);
                }
                entry2 = it2.hasNext() ? it2.next() : null;
            }
        } catch (SerializationException e) {
            throw new KVSException("Failed to dump SSTable to file", e);
        }
        out.flush();
        out.close();

        part1.raf.close();
        part2.raf.close();
        newPart.raf.close();
        if (!part1.file.delete()) {
            throw new KVSException(
                    String.format("Can't delete file %s", part1.file.getName()));
        }
        if (!part2.file.delete()) {
            throw new KVSException(
                    String.format("Can't delete file %s", part2.file.getName()));
        }
        if (!newPart.file.renameTo(part1.file.getAbsoluteFile())) {
            throw new KVSException(
                    String.format("Can't rename temp file %s", newPart.file.getName()));
        }
        newPart.file = part1.file;
        newPart.raf = new RandomAccessFile(newPart.file, "rw");
        return newPart;
    }
}
