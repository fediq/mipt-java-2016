package ru.mipt.java2016.homework.g595.shakhray.task2.ValidityCheck;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Реализуем сериализатор для файла
 */
public class HashSerialization {

    /*
    MD5 filename
     */
    private final String md5HashFilename = ".md5h";

    private static HashSerialization serialization = new HashSerialization();

    HashSerialization() { }

    public static HashSerialization getSerialization() {
        return serialization;
    }

//    void serialize(String directoryPath, String absoluteFilePath) {
//        String md5HashAbsolutePath = directoryPath + "/" + md5HashFilename;
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        try (InputStream is = Files.newInputStream(Paths.get(absoluteFilePath));
//             DigestInputStream dis = new DigestInputStream(is, md))
//        {
//        }
//        byte[] digest = md.digest();
//        RandomAccessFile file = new RandomAccessFile(new File(absoluteFilePath), "rw");
//
//    }
}
