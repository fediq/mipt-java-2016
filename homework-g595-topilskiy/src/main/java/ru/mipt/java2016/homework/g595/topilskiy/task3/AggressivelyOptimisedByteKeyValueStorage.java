package ru.mipt.java2016.homework.g595.topilskiy.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.ISerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task3.Serializer.RWStreamSerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task3.Verification.Adler32Verification;

import java.io.*;
import java.nio.channels.Channels;
import java.util.*;

/**
 * A KeyValueStorage implementation for <KeyType, ValueType> pair that uses
 *
 * - a storage.db      file  for storing <KeyType, ValueTypeLocation> pairs
 * -   storageN.db     files for storing ValueType information
 * - a storage_hash.db file  for storing hashes of Storage files (verification)
 *
 * - a buffered     <KeyType, ValueType> cache                       (for O(1) repeat access)
 * - a SSTable-like <KeyType, ValueTypeLocation> lookup table    (for O(logN) all-around access)
 *
 * @author Artem K. Topilskiy
 * @since 21.11.16
 */
public class AggressivelyOptimisedByteKeyValueStorage<KeyType, ValueType> implements KeyValueStorage<KeyType, ValueType> {
    /**
     *  Static Final Exception Strings
     */
    private static final String FAILED_FILE_CREATION = "Failure to create file";
    private static final String FAILED_FILE_LOCATION = "Failure to locate file";
    private static final String FAILED_FILE_READ     = "Failure to read file";
    private static final String FILE_CORRUPTED       = "File corrupted: checksums off";


    /**
     *  Static Final Data of Storage
     */
    /* The filename of the file of data storage */
    private static final String STORAGE_FILENAME = "storage.db";
    /* The filename prefix of the files of data storage */
    private static final String STORAGE_FILENAME_PREFIX = "storage";
    /* The filename of the file of the hashes for storage */
    private static final String HASH_FILENAME = "storage_hash.db";


    /**
     *  Final Data of Storage
     */
    /* The path to the directory of data storage */
    private final String pathToStorageDirectory;
    /* A Serializer for KeyType in KeyValueStorage */
    private final ISerializer keyTypeSerializer;
    /* A Serializer for ValueType in KeyValueStorage */
    private final ISerializer valueTypeSerializer;


    /**
     *  "Final" Functions of Storage
     */
    /**
     * @return the path to the directory of data storage
     */
    public String getPathToStorageDirectory() {
        return pathToStorageDirectory;
    }

    /**
     * @return the path to the main file of data storage
     */
    public String getPathToStorage() {
        return pathToStorageDirectory + File.separator + STORAGE_FILENAME;
    }

    /**
     * @return the name of the File with the corresponding fileIndex
     */
    private String getPathToStorageFileOfIndex(Integer fileIndex) {
        if (fileIndex == -1) {
            return getPathToStorage();
        }
        return pathToStorageDirectory + File.separator +
                STORAGE_FILENAME_PREFIX + fileIndex.toString() + ".db";
    }

    /**
     * @return the path to the hash file of data storage
     */
    public String getPathToHashStorage() {
        return pathToStorageDirectory + File.separator + HASH_FILENAME;
    }

    /**
     * @return the concatenated serializer class strings for improved verification
     */
    public String getSerializerClassStringVerification() {
        return keyTypeSerializer.getClass() + " " + valueTypeSerializer.getClass();
    }


    /**
     *  Dynamic Data of Storage
     */
    /* Boolean whether Storage is Closed */
    private Boolean isClosed;

    /* A set for storing all valid keys */
    private HashSet<KeyType> validKeys;
    /* A list of RAF of Storage in use for storing Values */
    private ArrayList<RandomAccessFile> rafStorageFiles;

    /* Map of Keys and the locations of Values in Storage */
    private HashMap<KeyType, ValueFileAndOffset> mapKeyValueLocation;

    /* Class for keeping the file and offset of where the Value is stored */
    private class ValueFileAndOffset {
        /* number of the file, in which to look for Value */
        private int  file;
        /* offset from the start of the file, where to look for Value */
        private long offset;

        ValueFileAndOffset(int file, long offset) {
            this.file = file;
            this.offset = offset;
        }
    }


    /**
     *  Private Initialization Service Functions
     */

    /**
     * Wrapper for the standard file.createNewFile function
     *
     * @param  file - the file to be created
     * @return boolean - whether the file was created or not
     * @throws MalformedDataException - thrown in case of creationg failure
     */
    private boolean createNewFileWrapper(File file) throws MalformedDataException {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            throw new MalformedDataException(FAILED_FILE_CREATION);
        }
    }

    /**
     *  Initialize Storage with data from files:
     *  - create new ones if they don't exist
     *  - call initStorageFromFiles if they do exist
     */
    private void initializeFilesOfStorage() throws MalformedDataException {
        File storage = new File(getPathToStorage());
        File hashStorage = new File(getPathToHashStorage());

        if (createNewFileWrapper(storage)) {
            createNewFileWrapper(hashStorage);
        } else if (!hashStorage.exists()) {
            throw new MalformedDataException(FAILED_FILE_LOCATION);
        } else {
            validateStorageFiles();
            initializeStorageFromFiles();
        }
    }

    /**
     *  Validate the state of Storage Files
     */
    private void validateStorageFiles() throws MalformedDataException {
        Integer numberOfFiles;

        try (BufferedInputStream storageBIS =
                     new BufferedInputStream(new FileInputStream(getPathToStorage()))) {

            String serializerClassStringVerification = RWStreamSerializer.deserializeString(storageBIS);

            if (!serializerClassStringVerification.equals(getSerializerClassStringVerification())) {
                throw new MalformedDataException("This is invalid file");
            }

            numberOfFiles = RWStreamSerializer.deserializeInteger(storageBIS);

        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_READ);
        }

        try (BufferedInputStream storageHashBIS =
                     new BufferedInputStream(new FileInputStream(getPathToHashStorage()))) {

            Integer readNumbeOfFiles = RWStreamSerializer.deserializeInteger(storageHashBIS);

            if (!numberOfFiles.equals(readNumbeOfFiles)) {
                throw new MalformedDataException("There are invalid data base");
            }

            for (Integer fileIndex = 0; fileIndex < numberOfFiles; ++fileIndex) {
                Long readChecksum = RWStreamSerializer.deserializeLong(storageHashBIS);
                Long calculatedChecksum =
                        Adler32Verification.calculateAdler32Checksum(getPathToStorageFileOfIndex(fileIndex));

                if (!readChecksum.equals(calculatedChecksum)) {
                    throw new MalformedDataException(FILE_CORRUPTED);
                }
            }

        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_READ);
        }
    }

    /**
     *  Initialize Storage from Storage Files
     */
    private void initializeStorageFromFiles() throws MalformedDataException {
        try (BufferedInputStream readFromFile =
                     new BufferedInputStream(new FileInputStream(getPathToStorage()))) {

            String serializerClassStringVerification = RWStreamSerializer.deserializeString(readFromFile);
            Integer numberOfFiles = RWStreamSerializer.deserializeInteger(readFromFile);

            for (Integer fileIndex = 0; fileIndex < numberOfFiles; ++fileIndex) {
                File currentFile = new File(getPathToStorageFileOfIndex(fileIndex));

                if (!currentFile.exists()) {
                    throw new MalformedDataException(FAILED_FILE_LOCATION);
                }

                rafStorageFiles.add(new RandomAccessFile(currentFile, "rw"));
            }

            Integer numberOfKeys = RWStreamSerializer.deserializeInteger(readFromFile);

            for (Integer keyNumber = 0; keyNumber < numberOfKeys; ++keyNumber) {
                KeyType readKey = (KeyType) RWStreamSerializer.deserialize(keyTypeSerializer, readFromFile);
                Integer readFile = RWStreamSerializer.deserializeInteger(readFromFile);
                Long readOffset = RWStreamSerializer.deserializeLong(readFromFile);

                mapKeyValueLocation.put(readKey, new ValueFileAndOffset(readFile, readOffset));

                validKeys.add(readKey);
            }

        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_READ);
        }
    }



    /**
     *  Private Runtime Service Functions
     */

    /**
     * Check whether Storage is closed
     * Throw an exception if it is
     *
     * @throws MalformedDataException if the Storage is closed
     */
    private void checkNotClosed() throws MalformedDataException {
        if (isClosed) {
            throw new MalformedDataException("Storage is Closed.");
        }
    }


    /**
     *  Public Functions
     */

    /**
     * Standard constructor of storage initialization
     *
     * @param pathToStorageDirectory - path to Storage Directory
     * @param keyTypeSerializer      - Serializer of Key Type
     * @param valueTypeSerializer    - Serializer of Value Type
     * @throws MalformedDataException - if Storage encounters an error (IO or Verification)
     */
    public AggressivelyOptimisedByteKeyValueStorage(String pathToStorageDirectory,
                                                    ISerializer keyTypeSerializer,
                                                    ISerializer valueTypeSerializer) throws MalformedDataException {

        this.pathToStorageDirectory = pathToStorageDirectory;
        this.keyTypeSerializer = keyTypeSerializer;
        this.valueTypeSerializer = valueTypeSerializer;
        isClosed = false;

        mapKeyValueLocation = new HashMap<>();
        validKeys       = new HashSet<>();
        rafStorageFiles = new ArrayList<>();

        initializeFilesOfStorage();
    }

    /**
     * Return the Value with the corresponding Key from Storage
     *
     * @param  key - Key of the Value being read
     * @return Value with the corresponding Key from Storage
     * @throws MalformedDataException if the Storage is closed
     */
    @Override
    public ValueType read(KeyType key) throws MalformedDataException {
        checkNotClosed();

        ValueType readValue = null;

        if (mapKeyValueLocation.keySet().contains(key)) {
            Integer file = mapKeyValueLocation.get(key).file;
            Long offset  = mapKeyValueLocation.get(key).offset;
            RandomAccessFile currentFile = rafStorageFiles.get(file);

            try {
                currentFile.seek(offset);
                InputStream inStream = Channels.newInputStream(currentFile.getChannel());
                readValue = (ValueType) RWStreamSerializer.deserialize(valueTypeSerializer, inStream);

            } catch (IOException caught) {
                throw new MalformedDataException(FAILED_FILE_READ);
            }

        }

        return readValue;
    }

    /**
     * Return whether a Value with Key exists in Storage
     *
     * @param key - Key of the Value being Found
     * @return Boolean == Value with Key exists in Storage
     * @throws MalformedDataException if the Storage is closed
     */
    @Override
    public boolean exists(KeyType key) throws MalformedDataException {
        checkNotClosed();
        return validKeys.contains(key);
    }

    /**
     * Write into Storage the pair <key, value>
     *
     * @param key   - Key of the element inserted
     * @param value - Value of the element inserted
     * @throws MalformedDataException if the Storage is closed
     */
    @Override
    public void write(KeyType key, ValueType value) throws MalformedDataException {
        checkNotClosed();

        if (value.equals(null)) {
            return;
        }

        validKeys.add(key);

        try {
            if (rafStorageFiles.size() == 0) {
                Integer newFileIndex = rafStorageFiles.size();
                String newFileName = getPathToStorageFileOfIndex(newFileIndex);
                createNewFileWrapper(new File(newFileName));
                rafStorageFiles.add(new RandomAccessFile(newFileName, "rw"));
            }

            RandomAccessFile newRAF = rafStorageFiles.get(0);
            newRAF.seek(newRAF.length());
            OutputStream inStream = Channels.newOutputStream(newRAF.getChannel());

            ValueFileAndOffset valueLocation =
                            new ValueFileAndOffset(0, newRAF.getFilePointer());

            mapKeyValueLocation.put(key, valueLocation);
            RWStreamSerializer.serialize(value, valueTypeSerializer, inStream);

        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_LOCATION);
        }
    }

    /**
     * Delete Value with Key from Storage
     *
     * @param key - Key of Value to be deleted
     * @throws MalformedDataException if the Storage is closed
     */
    @Override
    public void delete(KeyType key) throws MalformedDataException {
        checkNotClosed();

        mapKeyValueLocation.remove(key);
        validKeys.remove(key);
    }

    /**
     * Return an Iterator for Keys in Storage
     *
     * @return Iterator of the Keys in Storage
     * @throws MalformedDataException if the Storage is closed
     */
    @Override
    public Iterator<KeyType> readKeys() throws MalformedDataException {
        checkNotClosed();

        return validKeys.iterator();
    }

    /**
     * Return in the size (number on elements) in Storage
     *
     * @return Size of Storage
     * @throws MalformedDataException if the Storage is closed
     */
    @Override
    public int size() throws MalformedDataException {
        checkNotClosed();

        return validKeys.size();
    }

    /**
     * Close the current Storage:
     * - invalidate external Iterators
     * - write data to disk
     *
     * @throws MalformedDataException - if Storage encountered IO Problems whilst closing
     */
    @Override
    public void close() throws MalformedDataException {
        checkNotClosed();

        isClosed = true;

        try (BufferedOutputStream storageBOS =
                     new BufferedOutputStream(new FileOutputStream(getPathToStorage()))) {

            RWStreamSerializer.serializeString(getSerializerClassStringVerification(), storageBOS);
            RWStreamSerializer.serializeInteger(rafStorageFiles.size(), storageBOS);
            RWStreamSerializer.serializeInteger(mapKeyValueLocation.size(), storageBOS);

            for (Map.Entry<KeyType, ValueFileAndOffset> entry : mapKeyValueLocation.entrySet()) {
                RWStreamSerializer.serialize(entry.getKey(), keyTypeSerializer, storageBOS);
                RWStreamSerializer.serializeInteger(entry.getValue().file, storageBOS);
                RWStreamSerializer.serializeLong(entry.getValue().offset, storageBOS);
            }
        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_LOCATION);
        }

        try (BufferedOutputStream storageHashBOS =
                     new BufferedOutputStream(new FileOutputStream(getPathToHashStorage()))) {
            RWStreamSerializer.serializeInteger(rafStorageFiles.size(), storageHashBOS);

            for (Integer fileIndex = 0; fileIndex < rafStorageFiles.size(); ++fileIndex) {
                Long currentFileChecksum =
                        Adler32Verification.calculateAdler32Checksum(getPathToStorageFileOfIndex(fileIndex));
                RWStreamSerializer.serializeLong(currentFileChecksum, storageHashBOS);
            }
        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_LOCATION);
        }

        try {
            for (RandomAccessFile currentFile : rafStorageFiles) {
                currentFile.close();
            }
        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_LOCATION);
        }
    }
}