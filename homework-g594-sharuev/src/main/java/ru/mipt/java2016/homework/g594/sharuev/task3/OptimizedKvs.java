package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Организация этой штуковины:
 * cache хранит последние обработанные пары. Поиск сначала осуществляется по нему.
 * Он поддерживается актуальным в процессе всех операций с хранилищем.
 * Последние записи при превышении некоторого порога удаляются.
 * В памяти хранится MemTable. Изменение и запись осуществляются в неё.
 * Поиск сначала по ней. Если не нашли, идём в последний из indexMaps, и так далее до первого.
 * Первый и есть вся база данных. Если все эти файлы слить с первым,
 * то получится нужная копия для персистентного хранения (ещё ключи добавить).
 *
 * @param <K>
 * @param <V>
 */
public class OptimizedKvs<K, V> implements
        ru.mipt.java2016.homework.base.task2.KeyValueStorage {

    /*private class Address {
        int fileIndex;
        Long offset;

        public Address(int fileIndexVal, long offsetVal) {
            fileIndex = fileIndexVal;
            offset = offsetVal;
        }
    }*/

    private class Part {

        Part(RandomAccessFile rafVal, File fileVal, SortedMap<K, Long> indexMapVal) {
            raf = rafVal;
            indexMap = indexMapVal;
            file = fileVal;
        }

        RandomAccessFile raf;
        File file;
        SortedMap<K, Long> indexMap;
    }

    private Map<K, V> memTable, cache; // TODO: cache
    private Set<K> indexTable;
    private RandomAccessFile keyStorageRaf;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private boolean isOpen;
    private final String DBName;
    private static String path;
    private final static int DumpThreshold = 100;
    // Каждая SST хранится в своём файле, и это они. Обращение по индексу.
    private Deque<Part> parts;
    private Comparator<K> comparator;
    private File lockFile;
    private int nextFileIndex = 0;
    private Validator validator;

    public OptimizedKvs(String path, SerializationStrategy<K> keySerializationStrategy,
                        SerializationStrategy<V> valueSerializationStrategy,
                        Comparator<K> comparator) throws KVSException {
        memTable = new TreeMap<>(comparator);
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        indexTable = new TreeSet<>(comparator);
        cache = new TreeMap<>(comparator);
        DBName = getClass().getName() + getClass().getName();
        parts = new ArrayDeque<>();
        this.path = path;
        this.comparator = comparator;
        validator = new Validator();

        // Создать lock-файл
        lockFile = Paths.get(path, DBName + Names.StorageLockSuff).toFile();
        try {
            if (!lockFile.createNewFile()) {
                throw new KVSException("Storage was already opened");
            }
        } catch (IOException e) {
            throw new KVSException("Failed to lockFile database");
        }

        // Проверить хэш/создать новый файл
        boolean isNew = false;
        File keyStorageFile = Paths.get(path, DBName + Names.KeyStorageNameSuff).toFile();
        File valueStorageFile = Paths.get(path, DBName + Names.ValueStorageNameSuff).toFile();
        try {
            boolean wasCreated = keyStorageFile.createNewFile();
            if (wasCreated) {
                isNew = true;
                if (!valueStorageFile.createNewFile()) {
                    throw new KVSException("Values file found but keys file is missing");
                }
            } else {
                if (!valueStorageFile.exists()) {
                    throw new KVSException("Keys file found but value file is missing");
                }
                validator.checkHash(path);
            }
        } catch (IOException e) {
            throw new KVSException("Failed to create file", e);
        }


        // Открыть файл
        try {
            keyStorageRaf = new RandomAccessFile(keyStorageFile, "rw");
            parts.addLast(new Part(new RandomAccessFile(valueStorageFile, "rw"),
                    valueStorageFile,
                    new TreeMap<>(comparator)));
        } catch (FileNotFoundException e) {
            throw new KVSException("File of database was deleted", e);
        }

        // Подгрузить данные с диска
        try {
            if (!isNew) {
                initDatabaseFromDisk();
            }
        } catch (SerializationException e) {
            throw new KVSException("Failed to read database", e);
        }

        isOpen = true;
    }

    private V readValFromDisk(Part part, long offset) {
        try {
            part.raf.seek(offset);
            DataInputStream dataInputStream = new DataInputStream(
                    Channels.newInputStream(part.raf.getChannel()));
            return valueSerializationStrategy.deserializeFromStream(dataInputStream);
        } catch (Exception e) {
            throw new KVSException("Failed to read from disk", e);
        }
    }

    /**
     * Возвращает значение, соответствующее ключу.
     * Сложность O().
     *
     * @param key - ключ, который нужно найти
     * @return Значение или null, если ключ не найден.
     */
    public Object read(Object key) {
        checkOpen();
        if (!indexTable.contains(key)) {
            return null;
        }
        V val = cache.get(key);
        if (val != null) {
            return val;
        }
        val = memTable.get(key);
        if (val != null) {
            return val;
        }
        // Если не нашли в памяти, ищем на диске, начиная с конца.
        Iterator<Part> it = parts.descendingIterator();
        while (it.hasNext()) {
            Part part = it.next();
            Long val2 = part.indexMap.get(key);
            if (val2 != null) {
                //cache.put((K) key, val);
                return readValFromDisk(part, val2);
            }
        }

        return null;
    }

    /**
     * Поиск ключа.
     * Сложность O(NlogN).
     *
     * @param key - ключ, который нужно найти.
     * @return true, если найден, false, если нет.
     */
    public boolean exists(Object key) {
        checkOpen();
        return indexTable.contains(key);
    }

    /**
     * Вставка пары ключ-значение.
     * Сложность O(1
     *
     * @param key
     * @param value
     */
    public void write(Object key, Object value) {
        checkOpen();
        memTable.put((K) key, (V) value);
        indexTable.add((K) key);
        if (memTable.size() > DumpThreshold) {
            dumpMemTableToFile();
        }
    }

    public void delete(Object key) {
        checkOpen();
        if (indexTable.contains(key)) {
            indexTable.remove(key);
        }
    }

    /**
     * Сложность: как у итератора по ключам TreeMap.
     *
     * @return итератор по ключам.
     */
    public Iterator readKeys() {
        checkOpen();
        return indexTable.iterator();
    }

    /**
     * Сложность O(1).
     *
     * @return количество хранимых пар
     */
    public int size() {
        checkOpen();
        return indexTable.size();
    }

    public void close() throws IOException {
        checkOpen();
        dumpDatabaseToFile();
        validator.writeHash();
        if (!lockFile.delete()) {
            throw new IOException("Can't delete lock file");
        }
        keyStorageRaf.close();
        isOpen = false;
    }


    /**
     * Считывание файла ключей в indexMap. Буферизуется.
     * @throws SerializationException
     */
    private void initDatabaseFromDisk() throws SerializationException {
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(
                    Channels.newInputStream(keyStorageRaf.getChannel())));

            long numberOfEntries = dataInputStream.readLong();

            // Считываем ключи и оффсеты соответствующих значений
            for (long i = 0; i < numberOfEntries; ++i) {
                K key = keySerializationStrategy.deserializeFromStream(dataInputStream);
                long offset = dataInputStream.readLong();
                parts.getLast().indexMap.put(key, offset);
                indexTable.add(key);
            }

        } catch (IOException e) {
            throw new SerializationException("Read failed", e);
        }
    }

    // Складывает текущую MemTable в следующий по счёту файл part'а. Заодно создаёт IndexTable для этого куска.
    private void dumpMemTableToFile() {
        try {
            File tempFile = Paths.get(path,
                    DBName + nextFileIndex + Names.StoragePartSuff).toFile();
            ++nextFileIndex;
            Part tempPart = new Part(new RandomAccessFile(
                    tempFile, "rw"),
                    tempFile,
                    new TreeMap<>(comparator));
            DataOutputStream dataOutputStream = new DataOutputStream(
                    Channels.newOutputStream(tempPart.raf.getChannel()));

            for (Map.Entry<K, V> entry : memTable.entrySet()) {
                try {
                    tempPart.indexMap.put(entry.getKey(), tempPart.raf.getFilePointer());
                    valueSerializationStrategy.serializeToStream(entry.getValue(),
                            dataOutputStream);
                } catch (SerializationException e) {
                    throw new IOException("Serialization error");
                }
            }
            parts.addLast(tempPart);
        } catch (IOException e) {
            throw new KVSException("Failed to dump memtable to file", e);
        }
    }

    private void dumpDatabaseToFile() throws IOException {
        keyStorageRaf.setLength(0);
        keyStorageRaf.seek(0);
        keyStorageRaf.writeLong(size());
        DataOutputStream keyDos = new DataOutputStream(
                Channels.newOutputStream(keyStorageRaf.getChannel()));

        dumpMemTableToFile();

        // Смержить всё один файл. После в единственном элементе indexMaps лежит
        // дерево из всех ключей с правильными оффсетами, а в partRAF - все соответствующие значения.
        while (parts.size() > 1) {
            mergeFiles();
        }

        // Пишем ключи и сдвиги.
        for (Map.Entry<K, Long> entry : parts.getFirst().indexMap.entrySet()) {
            try {
                keySerializationStrategy.serializeToStream(entry.getKey(), keyDos);
                keyDos.writeLong(entry.getValue());
            } catch (SerializationException e) {
                throw new IOException("Serialization error", e);
            }
        }
    }

    /**
     * Смерживание двух частей в одну.
     * Берутся две части из начала дека, мержатся и итоговая часть кладётся в начало дека.
     * Мержатся они при помощи временного файла, который в конце переименовывается в имя первого из сливавшихся файлов.
     * @throws IOException
     */
    private void mergeFiles() throws IOException {
        assert parts.size() >= 2;
        // 1 и 2 в хронологическом порядке
        Part part1 = parts.getFirst();
        parts.pollFirst();
        Part part2 = parts.getFirst();
        parts.pollFirst();

        File tempFile = Paths.get(path, DBName + "Temp" + Names.StoragePartSuff).toFile();
        if (!tempFile.createNewFile()) {
            throw new KVSException("Temp file already exists");
        }

        Part newPart = new Part(new RandomAccessFile(tempFile, "rw"), tempFile,
                new TreeMap<>(comparator));

        DataOutputStream out = new DataOutputStream(
                Channels.newOutputStream(newPart.raf.getChannel()));

        Map.Entry<K, Long> entry1, entry2;
        Iterator<Map.Entry<K, Long>> it1 = part1.indexMap.entrySet().iterator(),
                it2 = part2.indexMap.entrySet().iterator();
        try {
            entry1 = it1.hasNext() ? it1.next() : null;
            entry2 = it2.hasNext() ? it2.next() : null;
            while (entry1 != null && entry2 != null) {
                if (!indexTable.contains(entry1.getKey())) {
                    entry1 = it1.hasNext() ? it1.next() : null;
                    continue;
                }
                if (!indexTable.contains(entry2.getKey())) {
                    entry2 = it2.hasNext() ? it2.next() : null;
                    continue;
                }
                if (comparator.compare(entry1.getKey(), entry2.getKey()) <= 0) {
                    newPart.indexMap.put(entry1.getKey(), newPart.raf.getFilePointer());
                    valueSerializationStrategy.serializeToStream(
                            readValFromDisk(part1, entry1.getValue()), out);
                    entry1 = it1.hasNext() ? it1.next() : null;
                } else { // if <=, поэтому из равных будет записан последний
                    newPart.indexMap.put(entry2.getKey(), newPart.raf.getFilePointer());
                    valueSerializationStrategy.serializeToStream(
                            readValFromDisk(part2, entry2.getValue()), out);
                    entry2 = it2.hasNext() ? it2.next() : null;
                }
            }
            while (entry1 != null) {
                if (indexTable.contains(entry1.getKey())) {
                    newPart.indexMap.put(entry1.getKey(), newPart.raf.getFilePointer());
                    valueSerializationStrategy.serializeToStream(
                            readValFromDisk(part1, entry1.getValue()), out);
                }
                entry1 = it1.hasNext() ? it1.next() : null;
            }
            while (entry2 != null) {
                if (indexTable.contains(entry2.getKey())) {
                    newPart.indexMap.put(entry2.getKey(), newPart.raf.getFilePointer());
                    valueSerializationStrategy.serializeToStream(
                            readValFromDisk(part2, entry2.getValue()), out);
                }
                entry2 = it2.hasNext() ? it2.next() : null;
            }
        } catch (SerializationException e) {
            throw new KVSException("Failed to dump SSTable to file", e);
        }

        part1.raf.close();
        part2.raf.close();
        newPart.raf.close();
        if (!part1.file.delete()) {
            throw new KVSException(String.format("Can't delete file %s", part1.file.getName()));
        }
        if (!part2.file.delete()) {
            throw new KVSException(String.format("Can't delete file %s", part2.file.getName()));
        }
        if (!newPart.file.renameTo(part1.file)) {
            throw new KVSException(String.format("Can't rename temp file %s", newPart.file.getName()));
        }
        newPart.file = tempFile;
        newPart.raf = new RandomAccessFile(newPart.file.getName(), "rw");
        parts.addFirst(newPart);
    }

    private void checkOpen() {
        if (!isOpen) {
            throw new RuntimeException("Can't access closed storage");
        }
    }

    private class Validator {
        private byte[] countHash() throws KVSException {
            try {
                // Создаём считатель хэша.
                MessageDigest md;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("MD5 algorithm can't be found");
                }

                // Хэш файла ключей
                try (InputStream is = new FileInputStream(
                        Paths.get(path, DBName + Names.KeyStorageNameSuff).toFile());
                     DigestInputStream dis = new DigestInputStream(is, md)) {
                    int c;
                    do {
                        c = dis.read();
                    } while (c != -1);
                }
                // Хэш файла значений
                try (InputStream is = new FileInputStream(
                        Paths.get(path, DBName + Names.ValueStorageNameSuff).toFile());
                     DigestInputStream dis = new DigestInputStream(is, md)) {
                    int c;
                    do {
                        c = dis.read();
                    } while (c != -1);
                }

                return md.digest();
            } catch (FileNotFoundException e) {
                throw new KVSException(
                        String.format("Can't find hash file %s", DBName + Names.StorageHashSuff));
            } catch (IOException e) {
                throw new KVSException("Some IO error while reading hash");
            }
        }

        // Проверяет хэш сразу двух файлов.
        private void checkHash(String path) throws KVSException {
            File hashFile = Paths.get(path, DBName + Names.StorageHashSuff).toFile();
            try {
                // Читаем файл хэша в буфер.
                ByteArrayOutputStream hashString = new ByteArrayOutputStream();
                try (InputStream ifs = new FileInputStream(hashFile)) {
                    int c;
                    while ((c = ifs.read()) != -1) {
                        hashString.write(c);
                    }
                }

                // Проверка.
                byte[] digest = countHash();
                if (!Arrays.equals(digest, hashString.toByteArray())) {
                    throw new KVSException("Hash mismatch");
                }
            } catch (FileNotFoundException e) {
                throw new KVSException(
                        String.format("Can't find hash file %s", DBName + Names.StorageHashSuff));
            } catch (IOException e) {
                throw new KVSException("Some IO error while reading hash");
            }
        }

        private void writeHash() throws KVSException {
            try {
                File hashFile = Paths.get(path, DBName + Names.StorageHashSuff).toFile();
                //hashFile.createNewFile();

                byte[] digest = countHash();
                try (OutputStream os = new FileOutputStream(hashFile)) {
                    os.write(digest);
                }

            } catch (FileNotFoundException e) {
                throw new KVSException(
                        String.format("Can't find hash file %s", DBName + Names.StorageHashSuff));
            } catch (IOException e) {
                throw new KVSException("Some IO error while reading hash");
            }
        }
    }

    private static class Names {
        // Формат файла: V значение, ...
        private final static String ValueStorageNameSuff = "ValueStorage.db";
        // Формат файла: long количество ключей, K ключ, long сдвиг, ...
        private final static String KeyStorageNameSuff = "KeyStorage.db";
        private final static String StorageHashSuff = "StorageHash.db";
        private final static String StoragePartSuff = "Part.db";
        private final static String StorageLockSuff = "Lock.db";
    }
}
