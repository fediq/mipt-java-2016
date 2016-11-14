package ru.mipt.java2016.homework.g594.shevkunov.task3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages head-file
 * Created by shevkunov on 24.10.16.
 */
class NobodyReadNamesKeyValueStorageHeader<K> {
    private final NobodyReadNamesKeyValueStorageSerializator<K> serializator;
    private final HashMap<K, Long> offsets = new HashMap<>();
    private final File file;
    private String fileHDR;

    NobodyReadNamesKeyValueStorageHeader(String argsK, String argsV, String fileName) throws IOException {
        file = new File(fileName);
        serializator = new NobodyReadNamesKeyValueStorageSerializator<>(argsK);
        if (!file.exists()) {
            fileHDR = argsK + argsV;
            write();
        }

        FileInputStream in = new FileInputStream(file);
        byte[] read = new byte[(int) readLong(in)];
        in.read(read);
        fileHDR = new String(read);

        if (!fileHDR.equals(argsK + argsV)) {
            throw new RuntimeException("Bad file");
        }

        long records = readLong(in);
        for (int i = 0; i < records; ++i) {
            long size = readLong(in);
            byte[] keyBytes = new byte[(int) size];
            in.read(keyBytes);
            long keyOffset = readLong(in);
            offsets.put(serializator.deSerialize(keyBytes), keyOffset);
        }
    }

    private long readLong(FileInputStream in) throws IOException {
        byte[] bytes = new byte[8];
        in.read(bytes);
        return NobodyReadNamesKeyValueStorageSerializator.toLong(bytes);
    }

    public HashMap<K, Long> getMap() {
        return offsets;
    }

    public void write() throws IOException {
        file.delete();
        FileOutputStream out = new FileOutputStream(file);
        byte[] bytesFileHDR = fileHDR.getBytes();
        out.write(NobodyReadNamesKeyValueStorageSerializator.toBytes((Integer) bytesFileHDR.length));
        out.write(bytesFileHDR);

        out.write(NobodyReadNamesKeyValueStorageSerializator.toBytes((Integer) offsets.size()));
        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            byte[] bytes = serializator.serialize(entry.getKey());
            byte[] offsetBytes = NobodyReadNamesKeyValueStorageSerializator.toBytes(entry.getValue());
            out.write(NobodyReadNamesKeyValueStorageSerializator.toBytes((Integer) bytes.length));
            out.write(bytes);
            out.write(offsetBytes);
        }
    }
}
