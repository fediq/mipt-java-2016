package ru.mipt.java2016.homework.g595.shakhray.task2.Hashing;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * The purpose of this file is to help with checking
 * whether the file has been modified or not
 */
public class FileHasher {

//FileHasher    /**
//     * Dealing with concurrency
//     */
//    private static FileHasher hasher = new FileHasher();
//
//    private FileHasher();
//
//    public static FileHasher getHasher() {
//        return hasher;
//    }
//
//    private String hashString;
//
//    public byte[] hash(String filePath) {
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        try (InputStream is = Files.newInputStream(Paths.get(filePath)));
//             DigestInputStream dis = new DigestInputStream(is, md))
//        {
//
//        }
//        savedHash = md.digest();
//    }
//
//    public boolean checkFile(String filePath) {
//
//    }
}
