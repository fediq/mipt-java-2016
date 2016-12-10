package ru.mipt.java2016.homework.g595.topilskiy.task3.Verification;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.zip.CheckedInputStream;
import java.util.zip.Adler32;

/**
 * Singleton for verifying a file with Adler32
 *
 * @author Artem K. Topilskiy
 * @since 21.11.16
 */
public class Adler32Verification {
    /* The limit for the length of Adler32 checksumming */
    private static final Integer BUFFER_BYTE_LIMIT = 10 * 1024;

    /**
     * @return the calculated Adler32 checksum of filename
     */
    public static Long calculateAdler32Checksum(String filename) throws IOException {
        byte[] buffer = new byte[BUFFER_BYTE_LIMIT];

        FileInputStream fileInStream = new FileInputStream(filename);
        BufferedInputStream bufferedFileInStream = new BufferedInputStream(fileInStream);
        CheckedInputStream checkedFileInStream = new CheckedInputStream(bufferedFileInStream, new Adler32());

        while (true) {
            if (checkedFileInStream.read(buffer) < 0) {
                break;
            }
        }

        checkedFileInStream.close();
        bufferedFileInStream.close();
        fileInStream.close();

        return checkedFileInStream.getChecksum().getValue();
    }

    /**
     * @return boolean, whether the file of filename has the same checksum
     */
    public static boolean checkAdler32Checksum(String filename, long checksum) {
        boolean checksumSame = false;

        try {
            checksumSame = (checksum == calculateAdler32Checksum(filename));
        } catch (IOException caught) {
            checksumSame = false;
        }

        return checksumSame;
    }
}
