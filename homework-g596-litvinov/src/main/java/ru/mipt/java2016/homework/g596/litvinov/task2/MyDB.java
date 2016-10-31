package ru.mipt.java2016.homework.g596.litvinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

public class MyDB<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private Map<K, V> map = new HashMap<>();
    private MySerialization<K> keySerialiator;
    private MySerialization<V> valueSerializator;
    private String fileName;
    private String checksum;
    private File file;
    private File checksumFile;


    public MyDB(String name, String path, MySerialization<K> keySerialiator,
            MySerialization<V> valueSerializator) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Invalid path");
        }
        fileName = path + File.separator + name + ".db";
        file = new File(fileName);
        this.keySerialiator = keySerialiator;
        this.valueSerializator = valueSerializator;
        checksum = path + File.separator + name + ".md5";
        checksumFile = new File(checksum);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            compareCheckSum();
            scanAllData();
        }
    }

    private void compareCheckSum() {
        try (DataInputStream fileInput = new DataInputStream(new FileInputStream(checksumFile))) {
            if (fileInput.readUTF().equals(genrateChecksum(fileName))) {
                throw new IllegalStateException("Invalid file: wrong checksum");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Invalid file");
        }
    }

    private void scanAllData() {
        try (DataInputStream fileInput = new DataInputStream(new FileInputStream(file))) {
            map.clear();
            int totalNumOfNotes = fileInput.readInt();
            for (int i = 0; i < totalNumOfNotes; i++) {
                K key = keySerialiator.read(fileInput);
                V value = valueSerializator.read(fileInput);
                map.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Invalid data in file");
        }
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        try (DataOutputStream fileOutput = new DataOutputStream(new FileOutputStream(fileName))) {
            fileOutput.writeInt(map.size());
            for (HashMap.Entry<K, V> entry : map.entrySet()) {
                keySerialiator.write(fileOutput, entry.getKey());
                valueSerializator.write(fileOutput, entry.getValue());
            }
            map = null;

            if (!checksumFile.exists()) {
                checksumFile.createNewFile();
            }
            DataOutputStream chksumOutput = new DataOutputStream(new FileOutputStream(checksum));
            String chksum = genrateChecksum(fileName);
            chksumOutput.writeUTF(checksum);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Error: can't write to file");
        }
    }

    private void checkNotClosed() {
        if (map == null) {
            throw new IllegalStateException("Already closed");
        }
    }

    private String genrateChecksum(String datafile) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(datafile);
        byte[] dataBytes = new byte[1024];

        int nRead;

        while ((nRead = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nRead);
        }
        byte[] mdBytes = md.digest();

        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < mdBytes.length; i++) {
            sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
