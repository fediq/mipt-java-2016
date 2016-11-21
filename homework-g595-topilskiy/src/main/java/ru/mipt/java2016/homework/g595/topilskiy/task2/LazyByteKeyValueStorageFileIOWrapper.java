package ru.mipt.java2016.homework.g595.topilskiy.task2;

import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.ISerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.IntegerSerializerSingleton;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.StringSerializerSingleton;
import ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays.JoinArraysPrimitiveByte;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A File Input Output Wrapper for KeyValueStorage
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class LazyByteKeyValueStorageFileIOWrapper<KeyType, ValueType> {
    /* The filename of the file of data storage */
    private static final String STORAGE_FILENAME = "storage.db";
    /* A simple validation string */
    private static final String ZERO_VALIDATION = "2K16 VALIDATION";
    /* The path to the directory of data storage */
    private final String pathToStorageDirectory;
    /* A Serializer for KeyType in KeyValueStorage */
    private final ISerializer keyTypeSerializer;
    /* A Serializer for ValueType in KeyValueStorage */
    private final ISerializer valueTypeSerializer;

    public LazyByteKeyValueStorageFileIOWrapper(String pathToStorageDirectory,
                                                ISerializer keyTypeSerializer,
                                                ISerializer valueTypeSerializer) throws IOException {
        this.pathToStorageDirectory = pathToStorageDirectory;
        this.keyTypeSerializer = keyTypeSerializer;
        this.valueTypeSerializer = valueTypeSerializer;
        checkWhetherExistsAndDirectory(pathToStorageDirectory);
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
    public String getPathToStorageDirectory() {
        return pathToStorageDirectory;
    }

    /**
     * Return a File Type Object which reflects into
     * the desired file location of data storage
     *
     * @return File, reflecting the data storage file
     */
    private File getStorageDataFile() {
        return new File(pathToStorageDirectory +
                        File.separator +
                        STORAGE_FILENAME);
    }

    /**
     * Save hashMapBuffer to file STORAGE_FILENAME in the storage directory
     *
     * @param hashMapBuffer - Map to be saved to Disk
     * @throws IOException  - if writing to Disk was unsuccessful
     */
    public void write(HashMap<KeyType, ValueType> hashMapBuffer) throws IOException {
        File storageDataFile = getStorageDataFile();
        FileOutputStream storageDataFileOut = new FileOutputStream(storageDataFile);
        BufferedOutputStream storageDataBufferOut = new BufferedOutputStream(storageDataFileOut);

        FileOutputWrapper fileOutputWrapper = new FileOutputWrapper();
        fileOutputWrapper.write(hashMapBuffer, storageDataBufferOut);

        storageDataBufferOut.flush();
        storageDataBufferOut.close();
    }

    /**
     * Read HashMapBuffer from STORAGE_FILENAME in the storage directory
     *
     * @throws IOException  - if writing to Disk was unsuccessful
     * @return the HashMapBuffer of the read data
     */
    public HashMap<KeyType, ValueType> read() throws IOException {
        File storageDataFile = getStorageDataFile();
        HashMap<KeyType, ValueType> hashMapBuffer = new HashMap<>();

        boolean storageDataFileCreated = storageDataFile.createNewFile();
        if (!storageDataFileCreated) {
            FileInputWrapper fileInputWrapper = new FileInputWrapper();
            hashMapBuffer = fileInputWrapper.read(storageDataFile);
        }

        return hashMapBuffer;
    }

    /* Class wrapping everything needed to write to disk */
    private class FileOutputWrapper {
        private final IntegerSerializerSingleton integerTypeSerializer =
                      IntegerSerializerSingleton.getInstance();

        /**
         * Writes hashMapBuffer data in a SSTable-like format to FileOut
         *
         * @param hashMapBuffer - data in map form to be written
         * @param fileOut       - file-descriptor into which data is written
         * @throws IOException  - thrown by FileOutputStream.write()
         */
        public void write(HashMap<KeyType, ValueType> hashMapBuffer,
                          BufferedOutputStream fileOut)       throws IOException {
            byte[] zeroValidationStringNumBytesAndBytes =
                    getNumBytesAndBytes(ZERO_VALIDATION, StringSerializerSingleton.getInstance());
            byte[] keyOffsetMapSizeBytes =
                    integerTypeSerializer.serialize(hashMapBuffer.size());

            HashMap<KeyType, byte[]> keyKeyBytesMap   = getKeyKeyBytesMap(hashMapBuffer);
            HashMap<KeyType, byte[]> keyValueBytesMap = getKeyValueBytesMap(hashMapBuffer);

            int sizeofHeaderOffsetBytes = zeroValidationStringNumBytesAndBytes.length +
                                          keyOffsetMapSizeBytes.length;

            HashMap<KeyType, byte[]> keyOffsetBytesMap =
                    getKeyOffsetMap(sizeofHeaderOffsetBytes, keyKeyBytesMap, keyValueBytesMap);


            fileOut.write(zeroValidationStringNumBytesAndBytes);
            fileOut.write(keyOffsetMapSizeBytes);
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
                                   getNumBytesAndBytes(entry.getKey(), keyTypeSerializer));
            }

            return keyKeyBytesMap;
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
                                     getNumBytesAndBytes(entry.getValue(), valueTypeSerializer));
            }

            return keyValueBytesMap;
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
            HashMap<KeyType, byte[]> keyOffsetBytesMap = new HashMap<>();

            int numberOffsetBytes = sizeofHeaderOffsetBytes;

            for (Map.Entry<KeyType, byte[]> entry : keyKeyBytesMap.entrySet()) {
                numberOffsetBytes += entry.getValue().length;
            }

            numberOffsetBytes += IntegerSerializerSingleton.getIntegerByteSize() * keyKeyBytesMap.size();

            for (Map.Entry<KeyType, byte[]> entry : keyValueBytesMap.entrySet()) {
                keyOffsetBytesMap.put(entry.getKey(),
                                      integerTypeSerializer.serialize(numberOffsetBytes));
                numberOffsetBytes += entry.getValue().length;
            }

            return keyOffsetBytesMap;
        }

        /**
         * Serialize value with an extra length prefix for future linear reading
         *
         * @param value - element to be extra serialized (with length prefix)
         * @param typeSerializer - the serializer of value
         * @param <Type> - Type of the value
         * @return NumBytes + Value in binary form
         */
        private <Type> byte[] getNumBytesAndBytes(Type value, ISerializer typeSerializer) {
            byte[] valueBytes     = typeSerializer.serialize(value);
            byte[] valueNumBytes  = integerTypeSerializer.serialize(valueBytes.length);
            return JoinArraysPrimitiveByte.joinArrays(valueNumBytes, valueBytes);
        }
    }

    /* Class wrapping everything needed to read from disk */
    private class FileInputWrapper {
        /**
         * Read hashMapBuffer from storage File Descriptor
         *
         * @param  storageDataFile - File Descriptor to be read from
         * @return the Map read from the File Descriptor
         * @throws IOException - if file cannot be read properly
         */
        public HashMap<KeyType, ValueType> read(File storageDataFile) throws IOException {
            HashMap<KeyType, ValueType> hashMapBuffer = new HashMap<>();

            RandomAccessFile storageDataFileIn = new RandomAccessFile(storageDataFile, "r");
            HashMap<KeyType, Integer> keyOffsetMap = readkeyOffsetMap(storageDataFileIn);

            for (Map.Entry<KeyType, Integer> entry : keyOffsetMap.entrySet()) {
                storageDataFileIn.seek(entry.getValue());
                ValueType valueRead =
                        (ValueType) readType(valueTypeSerializer, storageDataFileIn);
                hashMapBuffer.put(entry.getKey(), valueRead);
            }

            return hashMapBuffer;
        }

        /**
         * Read the keyOffsetMap from fileIn
         *
         * @param  fileIn - RandomAccessFile to be read from
         * @return the key-offset map read from fileIn
         * @throws IOException - if file cannot be read properly
         */
        private HashMap<KeyType, Integer> readkeyOffsetMap(RandomAccessFile fileIn) throws IOException {
            String zeroValidationStringRead =
                    (String) readType(StringSerializerSingleton.getInstance(), fileIn);

            if (!zeroValidationStringRead.equals(ZERO_VALIDATION)) {
                throw new IOException("Storage Database corrupted.");
            }

            int keyOffsetMapSize = readInteger(fileIn);
            HashMap<KeyType, Integer> keyOffsetMap = new HashMap<>();
            for (int i = 0; i < keyOffsetMapSize; ++i) {
                KeyType keyRead = (KeyType) readType(keyTypeSerializer, fileIn);
                Integer offsetRead = readInteger(fileIn);
                keyOffsetMap.put(keyRead, offsetRead);
            }

            return keyOffsetMap;
        }

        /**
         * Read an Integer from fileIn
         *
         * @param  fileIn - RandomAccessFile to be read from
         * @return the read Integer
         * @throws IOException - if file cannot be read properly
         */
        private Integer readInteger(RandomAccessFile fileIn) throws IOException {
            byte[] integerBytes = new byte[IntegerSerializerSingleton.getIntegerByteSize()];
            fileIn.read(integerBytes);
            return IntegerSerializerSingleton.getInstance().deserialize(integerBytes);
        }

        /**
         * Read a Type value from fileIn
         *
         * @param  readTypeSerializer - the Serializer of Type to be read
         * @param  fileIn - RandomAccessFile to be read from
         * @return an Object read, which can be casted to Type
         * @throws IOException - if file cannot be read properly
         */
        private Object readType(ISerializer readTypeSerializer, RandomAccessFile fileIn) throws IOException {
            Integer lenRead = readInteger(fileIn);
            byte[] typeBytes = new byte[lenRead];
            fileIn.read(typeBytes);

            return readTypeSerializer.deserialize(typeBytes);
        }
    }
}
