package ru.mipt.java2016.homework.g595.romanenko.task3;

import ru.mipt.java2016.homework.g595.romanenko.task2.SSTable;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignature;

import java.io.*;
import java.nio.channels.Channels;
import java.util.*;
import java.util.function.Function;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task3
 *
 * @author Ilya I. Romanenko
 * @since 12.11.16
 **/
public class MergeableSSTable<K, V> extends SSTable<K, V> {

    private final Comparator<K> keyComparator;

    private static final int BUFFER_SIZE = 10 * 1024;
    private static final byte[] BUFFER = new byte[BUFFER_SIZE];

    public MergeableSSTable(String path,
                            SerializationStrategy<K> kSerializationStrategy,
                            SerializationStrategy<V> vSerializationStrategy,
                            FileDigitalSignature fileDigitalSignature,
                            Comparator<K> keyComparator) throws IOException {
        super(path, kSerializationStrategy, vSerializationStrategy, fileDigitalSignature);
        this.keyComparator = keyComparator;
    }


    public void merge(String path, String tableName, MergeableSSTable<K, V> another) {

        epochNumber++;

        List<K> newerKeys = getKeys(readKeys());
        List<K> olderKeys = getKeys(another.readKeys());
        sortedKeys.clear();

        int olderKeysPos = 0;
        for (K newerKey : newerKeys) {
            while (olderKeysPos < olderKeys.size()) {
                int compareResult = keyComparator.compare(newerKey, olderKeys.get(olderKeysPos));
                if (compareResult > 0) {
                    sortedKeys.add(olderKeys.get(olderKeysPos));
                    olderKeysPos++;
                } else {
                    if (compareResult == 0) { // remove old key
                        olderKeysPos++;
                    }
                    break;
                }
            }
            sortedKeys.add(newerKey);
        }
        while (olderKeysPos < olderKeys.size()) {
            sortedKeys.add(olderKeys.get(olderKeysPos));
            olderKeysPos += 1;
        }
        newerKeys.clear();
        olderKeys.clear();

        try {
            RandomAccessFile resultDB;

            resultDB = new RandomAccessFile(path, "rw");
            storage.seek(0);
            another.storage.seek(0);

            InputStream currentDBStream = Channels.newInputStream(storage.getChannel());
            BufferedInputStream currentDBInputStream = new BufferedInputStream(currentDBStream);

            InputStream anotherDBStream = Channels.newInputStream(another.storage.getChannel());
            BufferedInputStream anotherDBInputStream = new BufferedInputStream(anotherDBStream);

            IntegerSerializer integerSerializer = IntegerSerializer.getInstance();

            Function<K, Integer> getMergeValueSize = (K key) -> {
                if (exists(key)) {
                    return valueByteSize.get(key);
                }
                return another.valueByteSize.get(key);
            };

            BufferedOutputStream outputStream = new BufferedOutputStream(
                    Channels.newOutputStream(resultDB.getChannel()));

            Map<K, Integer> resultIndices = new HashMap<>();

            //doesn't need, because we can replace old table with new values
            //Map<K, Integer> resultValueByteSize = new HashMap<>();

            integerSerializer.serializeToStream(sortedKeys.size(), outputStream);

            int totalLength = integerSerializer.getBytesSize(sortedKeys.size());

            for (K key : sortedKeys) {
                totalLength += keySerializationStrategy.getBytesSize(key);
            }
            totalLength += 2 * integerSerializer.getBytesSize(0) * sortedKeys.size();
            int byteSize;


            for (K key : sortedKeys) {
                resultIndices.put(key, totalLength);
                keySerializationStrategy.serializeToStream(key, outputStream);
                integerSerializer.serializeToStream(totalLength, outputStream);
                byteSize = getMergeValueSize.apply(key);
                valueByteSize.put(key, byteSize);
                integerSerializer.serializeToStream(byteSize, outputStream);
                totalLength += byteSize;
            }

            int currentDBPos = 0;
            int anotherDBPos = 0;
            int offset;
            synchronized (BUFFER) {

                for (K key : sortedKeys) {
                    if (indices.containsKey(key)) {
                        offset = indices.get(key);
                        byteSize = valueByteSize.get(key);
                        currentDBInputStream.skip(offset - currentDBPos);
                        currentDBPos = offset;
                        while (byteSize > 0) {
                            currentDBInputStream.read(BUFFER, 0, Math.min(BUFFER_SIZE, byteSize));
                            outputStream.write(BUFFER, 0, Math.min(BUFFER_SIZE, byteSize));
                            currentDBPos += Math.min(BUFFER_SIZE, byteSize);
                            byteSize -= BUFFER_SIZE;
                        }
                    } else {
                        offset = another.indices.get(key);
                        byteSize = another.valueByteSize.get(key);
                        anotherDBInputStream.skip(offset - anotherDBPos);
                        anotherDBPos = offset;
                        while (byteSize > 0) {
                            anotherDBInputStream.read(BUFFER, 0, Math.min(BUFFER_SIZE, byteSize));
                            outputStream.write(BUFFER, 0, Math.min(BUFFER_SIZE, byteSize));
                            anotherDBPos += Math.min(BUFFER_SIZE, byteSize);
                            byteSize -= BUFFER_SIZE;
                        }
                    }
                }
            }
            outputStream.flush();
            currentDBInputStream.close();
            anotherDBInputStream.close();

            storage.close();
            storage = resultDB;

            //remove old file
            File delFile = new File(this.path);
            if (!delFile.delete()) {
                System.out.println("Can't erase old table file " + this.getPath());
            }
            delFile = new File(this.path + ".sign");
            delFile.delete();
            //end remove

            indices.clear();
            indices.putAll(resultIndices);

            this.dbName = tableName;
            this.path = path;
            needToSign = true;
            hasUncommittedChanges = false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    private List<K> getKeys(Iterator<K> it) {
        ArrayList<K> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

}
