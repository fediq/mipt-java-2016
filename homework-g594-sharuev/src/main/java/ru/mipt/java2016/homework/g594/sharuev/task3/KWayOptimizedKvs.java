package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class KWayOptimizedKvs<K, V> extends OptimizedKvs<K, V> {

    public KWayOptimizedKvs(String path, SerializationStrategy<K> keySerializationStrategy,
                            SerializationStrategy<V> valueSerializationStrategy,
                            Comparator<K> comparator) throws KVSException {
        super(path, keySerializationStrategy, valueSerializationStrategy, comparator);
    }

    private class MergePart implements Comparable {
        private K key;
        private int index;

        MergePart(K key, int index) {
            this.key = key;
            this.index = index;
        }

        @Override
        public int compareTo(Object o) {
            int cmp = comparator.compare(key, ((MergePart) o).key);
            if (cmp != 0) {
                return cmp;
            }
            return this.index < ((MergePart) o).index ? -1 : (this.index == ((MergePart) o).index ? 0 : 1);
        }
    }

    /**
     * Сложность O(Nlog(N))
     *
     * @throws IOException
     */
    protected void mergeFiles() throws IOException {
        assert parts.size() >= 2;

        PriorityQueue<MergePart> priorityQueue = new PriorityQueue<MergePart>();
        File bigFile = parts.getFirst().file;

        File tempFile = Paths.get(path,
                dbName + "Temp" + OptimizedKvs.Consts.STORAGE_PART_SUFF).toFile();
        if (!tempFile.createNewFile()) {
            throw new KVSException("Temp file already exists");
        }
        OptimizedKvs.Part newPart = new OptimizedKvs.Part(new RandomAccessFile(tempFile, "rw"),
                tempFile);
        DataOutputStream out = bdosFromRaf(newPart.raf, OptimizedKvs.Consts.BUFFER_SIZE);

        ArrayList<DataInputStream> diss = new ArrayList<>();
        ArrayList<Iterator<K>> iters = new ArrayList<>();
        int i = 0;
        for (OptimizedKvs.Part part : parts) {
            part.raf.seek(0);
            diss.add(bdisFromRaf(part.raf, OptimizedKvs.Consts.BUFFER_SIZE));
            Iterator<K> iter = part.keys.iterator();
            K firstKey = iter.hasNext() ? iter.next() : null;
            iters.add(iter);
            if (firstKey != null) {
                priorityQueue.add(new MergePart(firstKey, i));
            }
            ++i;
        }

        try {
            while (!priorityQueue.isEmpty()) {
                MergePart top = priorityQueue.peek();
                priorityQueue.poll();

                if (indexTable.containsKey(top.key)) {
                    newPart.keys.add(top.key);
                    newPart.offsets.add(out.size());
                    valueSerializationStrategy.serializeToStream(
                            valueSerializationStrategy.deserializeFromStream(diss.get(top.index)),
                            out);
                    if (iters.get(top.index).hasNext()) {
                        K nextKey = iters.get(top.index).next();
                        priorityQueue.add(new MergePart(nextKey, top.index));
                    }
                } else {
                    valueSerializationStrategy.deserializeFromStream(diss.get(top.index));
                    if (iters.get(top.index).hasNext()) {
                        K nextKey = iters.get(top.index).next();
                        priorityQueue.add(new MergePart(nextKey, top.index));
                    }
                }
            }
        } catch (SerializationException e) {
            throw new KVSException("Failed to dump SSTable to file", e);
        }

        out.flush();
        out.close();

        newPart.raf.close();
        OptimizedKvs.Part bigPart = parts.getFirst();
        parts.pollFirst();
        if (!newPart.file.renameTo(bigPart.file.getAbsoluteFile())) {
            throw new KVSException(
                    String.format("Can't rename temp file %s", newPart.file.getName()));
        }

        for (OptimizedKvs.Part part : parts) {
            if (!part.file.delete()) {
                throw new KVSException(
                        String.format("Can't delete file %s", part.file.getName()));
            }
        }
        newPart.file = bigFile;
        newPart.raf = new RandomAccessFile(newPart.file, "rw");

        indexTable.clear();
        for (int j = 0; j < newPart.keys.size(); ++j) {
            indexTable.put((K) newPart.keys.get(j),
                    new OptimizedKvs.Address(newPart, (int) newPart.offsets.get(j)));
        }

        parts.clear();
        parts.addFirst(newPart);
    }
}
