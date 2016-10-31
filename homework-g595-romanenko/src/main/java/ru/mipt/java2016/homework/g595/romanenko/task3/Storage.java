package ru.mipt.java2016.homework.g595.romanenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.romanenko.task2.MapProducer;
import ru.mipt.java2016.homework.g595.romanenko.task2.SSTable;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.StringSerializer;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;


/**
 * Типизированное хранилище
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/
public class Storage<K, V> implements KeyValueStorage<K, V> {

    /**
     * Internal structure
     */
    private final List<SSTable<K, V>> tables;
    private final MergerSST<K, V> mergerSST;
    private final String currentDirectoryPath;
    private final SerializationStrategy<K> keySerializationStrategy;
    private final SerializationStrategy<V> valueSerializationStrategy;

    /**
     * Cache variables
     */
    private final Map<K, V> cachedValues = new HashMap<>();
    private final Queue<K> roundRobin = new ArrayDeque<>();

    /**
     * Updated values
     */
    private final Map<K, V> updatedValues = new HashMap<>();


    /**
     * Constants
     */
    private final int maxCountObjectsInMemory = 2048;

    /**
     * Internal state
     */
    private boolean isClosed = false;
    private int epochNumber = 0;
    private int totalAmount = 0;

    public Storage(String path,
                   SerializationStrategy<K> keySerializationStrategy,
                   SerializationStrategy<V> valueSerializationStrategy,
                   MergerSST<K, V> mergerSST) {

        this.currentDirectoryPath = path;
        this.mergerSST = mergerSST;
        this.tables = new ArrayList<>();
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        String tableListDBPath = path + "/tableList.db";
        try {
            if (!readTableList(tableListDBPath)) {
                throw new RuntimeException();
            }
        } catch (IOException ignore) {
            throw new IllegalStateException("File tableList exist, but subtables are uncorrected");
            // all right, empty storage
        }

    }

    /**
     * Read tableList databse and validate all
     *
     * @param tableListDBPath path to tableListDB
     * @return true if all is ok, false otherwise
     * @throws IOException if has system io error
     */
    private boolean readTableList(String tableListDBPath) throws IOException {
        SSTable<String, String> tablesList = new SSTable<>(tableListDBPath,
                StringSerializer.getInstance(), StringSerializer.getInstance());

        HashSet<String> keySet = new HashSet<>();
        Iterator<String> it = tablesList.readKeys();
        while (it.hasNext()) {
            keySet.add(it.next());
        }
        if (keySet.isEmpty()) {
            tablesList.close();
            return true;
        }

        Integer amount = Integer.parseInt(tablesList.getValue("TablesListSize"));
        totalAmount = Integer.parseInt(tablesList.getValue("DBSize"));
        String[] indices = tablesList.getValue("TablesList").split(";");

        for (int i = 0; i < amount; i++) {
            tables.add(null);
        }


        for (String index : indices) {
            if (index.equals("")) {
                continue;
            }
            String tableName = tablesList.getValue(index);
            File file = new File(currentDirectoryPath + "/" + tableName);
            if (!(file.exists() && !file.isDirectory())) {
                return false;
            }

            int pos = Integer.parseInt(index);

            SSTable<K, V> tempTable = new SSTable<>(
                    currentDirectoryPath + "/" + tableName,
                    keySerializationStrategy,
                    valueSerializationStrategy);
            tempTable.setDatabaseName(tableName);

            tables.set(pos, tempTable);
        }

        tablesList.close();
        return true;
    }

    /**
     * Read value from storage and cache it.
     * Doesn't contains more than maxCountObjectsInMemory objects.
     * Takes O(log n) time.
     *
     * @param key key to find
     * @return value for this key and null if key doesn't exist
     */
    @Override
    public V read(K key) {
        checkClosed();
        V value = null;
        if (cachedValues.containsKey(key)) {
            value = cachedValues.get(key);
        }
        for (SSTable<K, V> table : tables) {
            if (table != null) {
                value = table.getValue(key);
                if (value != null) {
                    break;
                }
            }
        }
        if (value != null) {
            addCachedValue(key, value);
        }
        return value;
    }

    /**
     * Add value to cache
     * Doesn't contains more than maxCountObjectsInMemory objects.
     *
     * @param key   key to cache
     * @param value value to cache
     */
    private void addCachedValue(K key, V value) {
        roundRobin.add(key);
        if (roundRobin.size() > maxCountObjectsInMemory) {
            K rRKey = roundRobin.poll();
            cachedValues.remove(rRKey);
        }
        cachedValues.put(key, value);
    }

    /**
     * Check existing of key in storage
     * Takes O(log n) time.
     *
     * @param key key to find
     * @return true for existing key, false otherwise
     */
    @Override
    public boolean exists(K key) {
        checkClosed();
        if (cachedValues.containsKey(key)) {
            return true;
        }
        boolean isExists = false;
        for (SSTable<K, V> table : tables) {
            if (table.exists(key)) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }

    /**
     * Write key, value pair to storage
     * Update if they exist
     * Flip to disk if updatedValues size more than maxCountObjectsInMemory
     *
     * @param key   key to write
     * @param value value to write
     */
    @Override
    public void write(K key, V value) {
        checkClosed();
        epochNumber += 1;
        if (!exists(key)) {
            totalAmount++;
        }
        updatedValues.put(key, value);
        addCachedValue(key, value);

        //prepare to flip
        if (updatedValues.size() > maxCountObjectsInMemory) {
            flipUpdatedValues();
        }
    }

    private void flipUpdatedValues() {
        SSTable<K, V> flipTable;
        String mergedTableName = "flipDP_" + Instant.now().toString() + "_.db";

        try {
            flipTable = new SSTable<>(
                    currentDirectoryPath + "/" + mergedTableName,
                    keySerializationStrategy,
                    valueSerializationStrategy);
            flipTable.setDatabaseName(mergedTableName);
            flipTable.rewrite(new MapProducer<>(updatedValues));

        } catch (IOException exp) {
            System.out.println(exp.getMessage());
            exp.printStackTrace();
            throw new RuntimeException("Can't flip data to disk");
        }
        for (int i = 0; i < tables.size(); i++) {
            SSTable<K, V> toMergeTable = tables.get(i);
            tables.set(i, null);
            if (toMergeTable == null) {
                tables.set(i, flipTable);
                flipTable = null;
                break;
            }
            try {
                mergedTableName = "mergeDB_stage_" + Integer.toString(i)
                        + "_time_" + Instant.now().toString() + "_.db";
                //Merge 2 tables into one new
                SSTable<K, V> mergedTable = mergerSST.merge(currentDirectoryPath + "/" + mergedTableName,
                        flipTable, toMergeTable);
                mergedTable.setDatabaseName(mergedTableName);

                flipTable.close();
                toMergeTable.close();
                //remove 2 old tables
                removeOldTable(flipTable);
                removeOldTable(toMergeTable);
                flipTable = mergedTable;

            } catch (IOException exp) {
                System.out.println(exp.getMessage());
                exp.printStackTrace();
                throw new RuntimeException("Can't flip data to disk");
            }
        }
        if (flipTable != null) {
            tables.add(flipTable);
        }
        updatedValues.clear();
    }

    /**
     * Remove table file from disk
     *
     * @param table table to be removed
     */
    private void removeOldTable(SSTable<K, V> table) {
        table.close();
        File delFile = new File(table.getPath());
        if (!delFile.delete()) {
            System.out.println("Can't erase old table file " + table.getPath());
            throw new RuntimeException("Can't erase old table file");
        }
    }

    /**
     * Check is file closed
     */
    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("File is closed");
        }
    }

    /**
     * Delete key from storage
     *
     * @param key key to delete
     */
    @Override
    public void delete(K key) {
        checkClosed();
        epochNumber += 1;
        if (exists(key)) {
            totalAmount -= 1;
        }
        if (cachedValues.containsKey(key)) {
            cachedValues.remove(key);
        }
        tables.stream().filter(table -> table != null).forEach(table -> table.removeKeyFromIndexes(key));
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        tables.stream().filter(table -> table != null).forEach(table -> {
            Iterator<K> it = table.readKeys();
            while (it.hasNext()) {
                read(it.next());
            }
        });
        return cachedValues.keySet().iterator();
    }

    /**
     * Get size of storage
     *
     * @return size of storage
     */
    @Override
    public int size() {
        checkClosed();
        return totalAmount;
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        epochNumber += 1;
        isClosed = true;
        if (!updatedValues.isEmpty()) {
            flipUpdatedValues();
        }
        try {
            SSTable<String, String> tablesDB = new SSTable<>(
                    currentDirectoryPath + "/tableList.db",
                    StringSerializer.getInstance(),
                    StringSerializer.getInstance());

            Map<String, String> tablesListMap = new HashMap<>();
            tablesListMap.put("TablesListSize", Integer.toString(tables.size()));
            tablesListMap.put("DBSize", Integer.toString(totalAmount));
            StringBuilder tablesListString = new StringBuilder();

            for (int i = 0; i < tables.size(); i++) {
                SSTable<K, V> table = tables.get(i);
                if (table != null) {
                    tablesListString.append(";").append(Integer.toString(i));
                    tablesListMap.put(Integer.toString(i), table.getDatabaseName());
                    table.close();
                }
            }

            tablesListMap.put("TablesList", tablesListString.toString());


            tablesDB.rewrite(new MapProducer<>(tablesListMap));
            tablesDB.close();
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
            exp.printStackTrace();
        }
    }
}
