package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.*;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class UnsortedOptimizedKvs<K, V> extends OptimizedKvs<K, V> {

    public UnsortedOptimizedKvs(String path, SerializationStrategy<K> keySerializationStrategy,
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
        File tempFile = Paths.get(path,
                dbName + "Temp" + OptimizedKvs.Consts.STORAGE_PART_SUFF).toFile();
        if (!tempFile.createNewFile()) {
            throw new KVSException("Temp file already exists");
        }
        OptimizedKvs.Part newPart = new OptimizedKvs.Part(new RandomAccessFile(tempFile, "rw"),
                tempFile);
        //DataOutputStream out = bdosFromRaf(newPart.valueStorageRaf, 1000*Consts.MAX_VALUE_SIZE);
        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(newPart.file),
                        Consts.MAX_VALUE_SIZE * 100));

        File bigFile = parts.getFirst().file;
        Map<K, Address> newIndexTable = new TreeMap<K, Address>();

        try {
            while (parts.size() > 0) {
                Part curPart = parts.getLast();
                parts.pollLast();
                curPart.raf.seek(0);
                //DataInputStream dis = bdisFromRaf(curPart.valueStorageRaf, 100 * Consts.MAX_VALUE_SIZE);
                DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(curPart.file),
                                Consts.MAX_VALUE_SIZE * 100));

                Iterator<K> keyIter = curPart.keys.iterator();
                while (keyIter.hasNext()) {
                    K key = keyIter.next();
                    if (indexTable.remove(key) != null) {
                        newIndexTable.put(key, new Address(newPart, out.size()));
                        newPart.keys.add(key);
                        newPart.offsets.add(out.size());
                        valueSerializationStrategy.serializeToStream(
                                valueSerializationStrategy.deserializeFromStream(dis), out);
                    } else {
                        valueSerializationStrategy.deserializeFromStream(dis);
                    }

                }

                curPart.raf.close();
                if (!curPart.file.delete()) {
                    throw new KVSException(
                            String.format("Can't delete file %s", curPart.file.getName()));
                }
            }
        } catch (SerializationException e) {
            throw new IOException("Serialization error", e);
        }

        out.flush();
        newPart.raf.close();
        if (!newPart.file.renameTo(bigFile.getAbsoluteFile())) {
            throw new KVSException(
                    String.format("Can't rename temp file %s", newPart.file.getName()));
        }
        newPart.file = bigFile;
        newPart.raf = new RandomAccessFile(newPart.file, "rw");
        parts.addLast(newPart);
        indexTable = newIndexTable;
    }
}
