package ru.mipt.java2016.homework.g595.topilskiy.task2;

import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.ISerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.SerializerFactory;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.IntegerSerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.StringSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A File Input Output Wrapper for KeyValueStorage
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
class LazyByteKeyValueStorageFileIOWrapper<KeyType, ValueType> {
    /* The filename of the file of data storage */
    private static final String STORAGE_FILENAME = "storage.db";
    /* A Class storing information on the current storage */
    private final LazyByteKeyValueStorageInfo storageInfo;
    /* Serializer for KeyType */
    private final ISerializer keyTypeSerializer;
    /* Serializer for ValueType */
    private final ISerializer valueTypeSerializer;

    LazyByteKeyValueStorageFileIOWrapper(LazyByteKeyValueStorageInfo storageInfoInit) throws IOException {
        storageInfo = storageInfoInit;
        checkWhetherExistsAndDirectory(storageInfo.getPathToStorageDirectory());

        keyTypeSerializer     = SerializerFactory.getSerializer(storageInfo.getKeyTypeString());
        valueTypeSerializer   = SerializerFactory.getSerializer(storageInfo.getValueTypeString());
    }

    /**
     * Check whether a directory exists at pathToDirectory
     *
     * @param  pathToDirectory - path to be checked
     * @throws IOException - if no file exists at pathToDirectory, or if that file is not a directory
     */
    private void checkWhetherExistsAndDirectory(String pathToDirectory) throws IOException {
        File directory = new File(pathToDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException("Failed to read as a directory: " + pathToDirectory);
        }
    }

    /**
     * Return the path to the directory of data storage
     *
     * @return the path to the directory of data storage
     */
    String getPathToStorageDirectory() {
        return storageInfo.getPathToStorageDirectory();
    }

    /**
     * Save hashMapBuffer to file STORAGE_FILENAME in the storage directory
     *
     * @param hashMapBuffer - Map to be saved to Disk
     * @throws IOException  - if writing to Disk was unsuccessful
     */
    void write(HashMap<KeyType, ValueType> hashMapBuffer) throws IOException {
        File storageDataFile = new File(storageInfo.getPathToStorageDirectory() +
                                        File.pathSeparator +
                                        STORAGE_FILENAME);
        storageDataFile.createNewFile();

        FileOutputStream storageDataFileOut = new FileOutputStream(storageDataFile);

        FileOutputWrapper fileOutputWrapper = new FileOutputWrapper();
        fileOutputWrapper.write(hashMapBuffer, storageDataFileOut);
    }


    /* Class wrapping everything needed to write to disk */
    private class FileOutputWrapper {
        /**
         * Writes hashMapBuffer data in a SSTable-like format to FileOut
         *
         * @param hashMapBuffer - data in map form to be written
         * @param fileOut       - file-descriptor into which data is written
         * @throws IOException  - thrown by FileOutputStream.write()
         */
        void write(HashMap<KeyType, ValueType> hashMapBuffer,
                   FileOutputStream fileOut)                   throws IOException{
            StringSerializer  stringTypeSerializer  = new StringSerializer();
            IntegerSerializer integerTypeSerializer = new IntegerSerializer();

            byte[] keyTypeStringBytes =
                    stringTypeSerializer.serialize(storageInfo.getKeyTypeString());
            byte[] valueTypeStringBytes =
                    stringTypeSerializer.serialize((storageInfo.getValueTypeString()));
            byte[] KeyOffsetMapSizeBytes =
                    integerTypeSerializer.serialize(hashMapBuffer.size());

            HashMap<KeyType, byte[]> keyKeyBytesMap   = getKeyKeyBytesMap(hashMapBuffer);
            HashMap<KeyType, byte[]> keyValueBytesMap = getKeyValueBytesMap(hashMapBuffer);

            int sizeofHeaderOffsetBytes = keyTypeStringBytes.length +
                                          valueTypeStringBytes.length +
                                          KeyOffsetMapSizeBytes.length;


            HashMap<KeyType, byte[]> keyOffsetBytesMap =
                    getKeyOffsetMap(sizeofHeaderOffsetBytes, keyKeyBytesMap, keyValueBytesMap);

            fileOut.write(keyTypeStringBytes);
            fileOut.write(valueTypeStringBytes);
            fileOut.write(KeyOffsetMapSizeBytes);
            for (Map.Entry<KeyType, byte[]> entry : keyOffsetBytesMap.entrySet()) {
                KeyType currentKey = entry.getKey();
                fileOut.write(keyKeyBytesMap.get(currentKey));
                fileOut.write(entry.getValue());
            }
            for (Map.Entry<KeyType, byte[]> entry : keyValueBytesMap.entrySet()) {
                fileOut.write(entry.getValue());
            }
        }

        /**
         * Turn a ValueType containing map into a map,
         * containing the serialized version of ValueType
         *
         * @param hashMapBuffer - map to be value-converted-to-bytes
         * @return A map of Key pointing to valueBytes
         */
        private HashMap<KeyType, byte[]>
        getKeyValueBytesMap(HashMap<KeyType, ValueType> hashMapBuffer) {
            HashMap<KeyType, byte[]> keyValueBytesMap = new HashMap<>();

            for (Map.Entry<KeyType, ValueType> entry : hashMapBuffer.entrySet()) {
                keyValueBytesMap.put(entry.getKey(),
                                     valueTypeSerializer.serialize(entry.getValue()));
            }

            return keyValueBytesMap;
        }

        /**
         * Turn a KeyType containing map into a map,
         * containing the serialized version of KeyType
         *
         * @param hashMapBuffer - map to be key-converted-to-bytes
         * @return A map of Key pointing to keyBytes
         */
        private HashMap<KeyType, byte[]>
        getKeyKeyBytesMap(HashMap<KeyType, ValueType> hashMapBuffer) {
            HashMap<KeyType, byte[]> keyKeyBytesMap = new HashMap<>();

            for (Map.Entry<KeyType, ValueType> entry : hashMapBuffer.entrySet()) {
                keyKeyBytesMap.put(entry.getKey(),
                                   keyTypeSerializer.serialize(entry.getKey()));
            }

            return keyKeyBytesMap;
        }

        /**
         * Fill the KeyOffset Map with values of offsets in the to be created storage
         * and return it as a map of keys and byte[]
         *
         * @param sizeofHeaderOffsetBytes - size of header byte information
         * @param keyKeyBytesMap   - Map of Keys and their Byte representation
         * @param keyValueBytesMap - Map of Keys and
         *                           Byte representations of their Value pairs
         * @return A map of Key pointing to keyOffsetBytes
         */
        private HashMap<KeyType, byte[]>
        getKeyOffsetMap(int sizeofHeaderOffsetBytes,
                        HashMap<KeyType, byte[]> keyKeyBytesMap,
                        HashMap<KeyType, byte[]> keyValueBytesMap) {
            IntegerSerializer integerTypeSerializer = new IntegerSerializer();
            HashMap<KeyType, byte[]> keyOffsetBytesMap = new HashMap<>();

            int numberOffsetBytes = sizeofHeaderOffsetBytes;

            for (Map.Entry<KeyType, byte[]> entry : keyKeyBytesMap.entrySet()) {
                numberOffsetBytes += entry.getValue().length;
            }

            numberOffsetBytes += IntegerSerializer.getIntegerByteSize() * keyKeyBytesMap.size();

            for (Map.Entry<KeyType, byte[]> entry : keyValueBytesMap.entrySet()) {
                keyOffsetBytesMap.put(entry.getKey(),
                                      integerTypeSerializer.serialize(numberOffsetBytes));
                numberOffsetBytes += entry.getValue().length;
            }

            return keyOffsetBytesMap;
        }
    }
}
