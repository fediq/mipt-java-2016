package ru.mipt.java2016.homework.g595.topilskiy.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.ISerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task3.Serializer.RWStreamSerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task3.Verification.Adler32Verification;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A KeyValueStorage implementation for <KeyType, ValueType> pair that uses
 *
 * - a storage.db      file  for storing <KeyType, ValueTypeLocation> pairs
 * -   storageN.db     files for storing ValueType information
 * - a storage_hash.db file  for storing hashes of Storage files (verification)
 *
 * - a buffered     <KeyType, ValueType>         cache           (for O(1) repeat access)
 * - a SSTable-like <KeyType, ValueTypeLocation> lookup table    (for O(logN) all-around access)
 *
 * @author Artem K. Topilskiy
 * @since 21.11.16
 */
public class OptimisedByteKeyValueStorage<KeyType, ValueType> implements KeyValueStorage<KeyType, ValueType> {
    /**
     *  Static Final Exception Strings
     */
    private static final String FAILED_FILE_CREATION = "Failure to create file";
    private static final String FAILED_FILE_DELETION = "Failure to delete file";
    private static final String FAILED_FILE_LOCATION = "Failure to locate file";
    private static final String FAILED_FILE_READ     = "Failure to read file";
    private static final String FILE_CORRUPTED       = "File corrupted: checksums off";
    private static final String STORAGE_ALREADY_LOCKED = "Storage is locked: lock file already exists.";


    /**
     *  Static Final Data of Storage
     */
    /* The filename of the file of data storage */
    private static final String STORAGE_FILENAME = "storage.db";
    /* The filename prefix of the files of data storage */
    private static final String STORAGE_FILENAME_PREFIX = "storage";
    /* The filename of the file of the hashes for storage */
    private static final String HASH_FILENAME = "storage_hash.db";
    /* The filename of the file of the hashes for storage */
    private static final String LOCK_FILENAME = "storage.lock";
    /* Maximum size of buffer before the need for a Buffer Flush */
    private static final int MAX_BUFFER_SIZE = 128;
    /* Maximum number of bytes stored on a file before the need for a new file */
    private static final long MAX_FILE_BYTE_SIZE = 256 * 1024 * 1024;  /* 256MB */
    /* Maximum additional fraction of the storage size that can be dirty (filled with deleted values) */
    private static final double MAX_DIRTY_FRACTION = 2.0;

    /* Class for keeping the file and offset of where the Value is stored */
    private static class ValueFileAndOffset {
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
     *  Final Data of Storage
     */
    /* The path to the directory of data storage */
    private final String pathToStorageDirectory;
    /* A Serializer for KeyType in KeyValueStorage */
    private final ISerializer keyTypeSerializer;
    /* A Serializer for ValueType in KeyValueStorage */
    private final ISerializer valueTypeSerializer;
    /* A ReadWrite lock for thread-safety */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /* A */
    private final ReentrantLock fileLock = new ReentrantLock();
    /* A file for locking the storage on a filesystem level */
    private final File storageFilesystemLock;


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
     * @return the path to the lock file of data storage
     */
    public String getPathToLockStorage() {
        return pathToStorageDirectory + File.separator + LOCK_FILENAME;
    }

    /**
     * @return the concatenated serializer class strings for improved verification
     */
    private String getSerializerClassStringVerification() {
        return keyTypeSerializer.getClass() + " " + valueTypeSerializer.getClass();
    }


    /**
     *  Dynamic Data of Storage
     */
    /* Boolean whether Storage is Closed */
    private Boolean isClosed = false;
    /* Number of dirty (deleted) values in storage */
    private int numberDirtyValues;

    /* A buffer for intermediate data storage */
    private HashMap<KeyType, ValueType> bufferKeyVal = new HashMap<>();
    /* A list of RAF of Storage in use for storing Values */
    private ArrayList<RandomAccessFile> rafStorageFiles = new ArrayList<>();

    /* Map of Keys and the locations of Values in Storage */
    private HashMap<KeyType, ValueFileAndOffset> mapKeyValueLocation = new HashMap<>();


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

            numberDirtyValues = RWStreamSerializer.deserializeInteger(readFromFile);

            Integer numberOfKeys = RWStreamSerializer.deserializeInteger(readFromFile);

            for (Integer keyNumber = 0; keyNumber < numberOfKeys; ++keyNumber) {
                KeyType readKey = (KeyType) RWStreamSerializer.deserialize(keyTypeSerializer, readFromFile);
                Integer readFile = RWStreamSerializer.deserializeInteger(readFromFile);
                Long readOffset = RWStreamSerializer.deserializeLong(readFromFile);

                mapKeyValueLocation.put(readKey, new ValueFileAndOffset(readFile, readOffset));
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
     * @return whether the Storage needs to be cleaned of deleted values
     */
    private boolean checkStorageTooDirty() {
        if (mapKeyValueLocation.size() > MAX_BUFFER_SIZE) {
            return numberDirtyValues > mapKeyValueLocation.size() * MAX_DIRTY_FRACTION;
        } else {
            return numberDirtyValues > 4 * MAX_BUFFER_SIZE;
        }
    }

    /**
     * @return the index of the next suitable RAF of Storage to write to
     * @throws IOException if a new RAF file could not be created
     */
    private int getIndexOfRAFToWrite() throws IOException {
        if (rafStorageFiles.size() != 0) {
            RandomAccessFile currRAF = rafStorageFiles.get(rafStorageFiles.size() - 1);
            if (currRAF.length() <= MAX_FILE_BYTE_SIZE) {
                return rafStorageFiles.size() - 1;
            }
        }

        Integer newFileIndex = rafStorageFiles.size();
        String  newFileName  = getPathToStorageFileOfIndex(newFileIndex);
        File newFile = new File(newFileName);

        createNewFileWrapper(newFile);

        rafStorageFiles.add(new RandomAccessFile(newFileName, "rw"));
        RandomAccessFile newRAF = rafStorageFiles.get(newFileIndex);
        newRAF.setLength(0);

        return newFileIndex;
    }

    /**
     *  Flush bufferKeyVal to disk
     *  NOTE: only called within an existing write lock
     */
    private void flushBuffer() {
        readWriteLock.writeLock().lock();
        try {
            int indexRAFToWrite = getIndexOfRAFToWrite();
            RandomAccessFile usedRAF = rafStorageFiles.get(indexRAFToWrite);

            usedRAF.seek(usedRAF.length());

            OutputStream outStream = Channels.newOutputStream(usedRAF.getChannel());

            for (Map.Entry<KeyType, ValueType> entry: bufferKeyVal.entrySet()) {
                ValueFileAndOffset valueLocation =
                        new ValueFileAndOffset(indexRAFToWrite, usedRAF.getFilePointer());

                mapKeyValueLocation.put(entry.getKey(), valueLocation);
                RWStreamSerializer.serialize(entry.getValue(), valueTypeSerializer, outStream);
            }
            bufferKeyVal.clear();

        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_LOCATION);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     *  Rebuild Storage Value files from scratch
     *  (retaining all data, except deleted values)
     *  NOTE: only called within an existing write lock
     */
    private void rebuildStorage() throws MalformedDataException {
        readWriteLock.writeLock().lock();
        try {
            if (bufferKeyVal.size() > 0) {
                flushBuffer();
            }

            HashMap<KeyType, ValueFileAndOffset> newMapKeyValueLocation =
                    new HashMap<KeyType, ValueFileAndOffset>();

            ArrayList<File> newFiles = new ArrayList<>();


            File currFile = new File(getPathToStorageFileOfIndex(newFiles.size()) + "tmp");
            newFiles.add(currFile);
            RandomAccessFile currRAF = new RandomAccessFile(currFile, "rw");
            OutputStream outStream = Channels.newOutputStream(currRAF.getChannel());
            currRAF.setLength(0);
            currRAF.seek(0);

            for (Map.Entry<KeyType, ValueFileAndOffset> entry : mapKeyValueLocation.entrySet()) {
                if (currRAF.length() > MAX_FILE_BYTE_SIZE) {
                    outStream.close();
                    currRAF.close();

                    currFile = new File(getPathToStorageFileOfIndex(newFiles.size()) + "tmp");
                    newFiles.add(currFile);
                    currRAF = new RandomAccessFile(currFile, "rw");
                    outStream = Channels.newOutputStream(currRAF.getChannel());
                    currRAF.setLength(0);
                    currRAF.seek(0);
                }

                RandomAccessFile currValueFile = rafStorageFiles.get(entry.getValue().file);
                currValueFile.seek(entry.getValue().offset);
                InputStream inStream = Channels.newInputStream(currValueFile.getChannel());

                ValueType currValue =
                        (ValueType) RWStreamSerializer.deserialize(valueTypeSerializer, inStream);

                ValueFileAndOffset newValueLocation =
                        new ValueFileAndOffset(newFiles.size() - 1, currRAF.getFilePointer());

                newMapKeyValueLocation.put(entry.getKey(), newValueLocation);
                RWStreamSerializer.serialize(currValue, valueTypeSerializer, outStream);
            }


            for (RandomAccessFile currentFile : rafStorageFiles) {
                currentFile.close();
            }
            rafStorageFiles.clear();

            for (Integer fileIndex = 0; fileIndex < rafStorageFiles.size(); ++fileIndex) {
                Path currPath = Paths.get(getPathToStorageFileOfIndex(fileIndex));
                Files.delete(currPath);
            }


            for (Integer fileIndex = 0; fileIndex < newFiles.size(); ++fileIndex) {
                Path currPath = Paths.get(getPathToStorageFileOfIndex(fileIndex) + "tmp");
                Path destPath = Paths.get(getPathToStorageFileOfIndex(fileIndex));
                Files.move(currPath, destPath);
            }

            for (File newFile : newFiles) {
                rafStorageFiles.add(new RandomAccessFile(newFile, "rw"));
            }
            numberDirtyValues = 0;
            mapKeyValueLocation = newMapKeyValueLocation;

        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_LOCATION);
        } finally {
            readWriteLock.writeLock().unlock();
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
    public OptimisedByteKeyValueStorage(String pathToStorageDirectory,
                                        ISerializer keyTypeSerializer,
                                        ISerializer valueTypeSerializer) throws MalformedDataException {

        this.pathToStorageDirectory = pathToStorageDirectory;
        this.keyTypeSerializer = keyTypeSerializer;
        this.valueTypeSerializer = valueTypeSerializer;

        storageFilesystemLock = new File(getPathToLockStorage());
        try {
            if (!storageFilesystemLock.createNewFile()) {
                throw new MalformedDataException(STORAGE_ALREADY_LOCKED);
            }
        } catch (IOException caught) {
            throw new MalformedDataException(FAILED_FILE_CREATION);
        }

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

        readWriteLock.readLock().lock();
        try {
            ValueType readValue = null;

            if (bufferKeyVal.keySet().contains(key)) {
                readValue = bufferKeyVal.get(key);

            } else if (mapKeyValueLocation.keySet().contains(key)) {
                Integer file = mapKeyValueLocation.get(key).file;
                Long offset = mapKeyValueLocation.get(key).offset;
                RandomAccessFile currentFile = rafStorageFiles.get(file);

                fileLock.lock();
                try {
                    currentFile.seek(offset);
                    InputStream inStream = Channels.newInputStream(currentFile.getChannel());
                    readValue = (ValueType) RWStreamSerializer.deserialize(valueTypeSerializer, inStream);

                } catch (IOException caught) {
                    throw new MalformedDataException(FAILED_FILE_READ);
                } finally {
                    fileLock.unlock();
                }
            }

            return readValue;

        } finally {
            readWriteLock.readLock().unlock();
        }
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

        readWriteLock.readLock().lock();
        try {
            /* mapKeyValueLocation has all of the keys of bufferKeyVal, but the latter has less elements,
             * so a search there first will be much faster in the best case than
             * just looking straight into mapKeyValueLocation
             */
            return bufferKeyVal.containsKey(key) || mapKeyValueLocation.containsKey(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
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

        readWriteLock.writeLock().lock();
        try {
            /* If the old value will have to be deleted */
            if (mapKeyValueLocation.containsKey(key) && !bufferKeyVal.containsKey(key)) {
                ++numberDirtyValues;
            }

            bufferKeyVal.put(key, value);
            mapKeyValueLocation.put(key, null);

            if (bufferKeyVal.size() >= MAX_BUFFER_SIZE) {
                flushBuffer();
            }

            if (checkStorageTooDirty()) {
                rebuildStorage();
            }

        } finally {
            readWriteLock.writeLock().unlock();
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

        readWriteLock.writeLock().lock();
        try {
            if (mapKeyValueLocation.containsKey(key)) {
                ++numberDirtyValues;

                mapKeyValueLocation.remove(key);
            }

            bufferKeyVal.remove(key);

            if (checkStorageTooDirty()) {
                rebuildStorage();
            }

        } finally {
            readWriteLock.writeLock().unlock();
        }
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

        readWriteLock.readLock().lock();
        try {
            return mapKeyValueLocation.keySet().iterator();
        } finally {
            readWriteLock.readLock().unlock();
        }
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

        readWriteLock.readLock().lock();
        try {
            return mapKeyValueLocation.size();
        } finally {
            readWriteLock.readLock().unlock();
        }
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
        if (isClosed) {
            return;
        }

        readWriteLock.writeLock().lock();
        try {
            isClosed = true;
            flushBuffer();

            if (checkStorageTooDirty()) {
                rebuildStorage();
            }

            try (BufferedOutputStream storageBOS =
                         new BufferedOutputStream(new FileOutputStream(getPathToStorage()))) {

                RWStreamSerializer.serializeString(getSerializerClassStringVerification(), storageBOS);
                RWStreamSerializer.serializeInteger(rafStorageFiles.size(), storageBOS);
                RWStreamSerializer.serializeInteger(numberDirtyValues, storageBOS);
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

            try {
                Files.delete(storageFilesystemLock.toPath());
            } catch (IOException caught) {
                throw new MalformedDataException(FAILED_FILE_DELETION);
            }

        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}