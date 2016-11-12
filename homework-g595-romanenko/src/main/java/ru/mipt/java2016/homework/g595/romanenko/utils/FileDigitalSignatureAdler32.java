package ru.mipt.java2016.homework.g595.romanenko.utils;

import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.LongSerializer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * ru.mipt.java2016.homework.g595.romanenko.utils
 *
 * @author Ilya I. Romanenko
 * @since 10.11.16
 **/
public class FileDigitalSignatureAdler32 implements FileDigitalSignature {

    private static final LongSerializer LONG_SERIALIZER = LongSerializer.getInstance();

    private static final FileDigitalSignatureAdler32 INSTANCE = new FileDigitalSignatureAdler32();

    public static FileDigitalSignatureAdler32 getInstance() {
        return INSTANCE;
    }

    private FileDigitalSignatureAdler32() {
    }

    final int bufferSize = 10 * 1024;
    private final byte[] buffer = new byte[bufferSize];


    @Override
    public byte[] getFileSign(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bufferedReader = new BufferedInputStream(fis);
        CheckedInputStream checkedInputStream = new CheckedInputStream(bufferedReader, new Adler32());

        while (true) {
            if (checkedInputStream.read(buffer) < 0) {
                break;
            }
        }

        checkedInputStream.close();
        bufferedReader.close();
        fis.close();

        return LONG_SERIALIZER.serializeToBytes(checkedInputStream.getChecksum().getValue());
    }

    @Override
    public boolean validateFileSign(String path, byte[] sign) {
        boolean validationResult;

        try {
            FileInputStream fis = new FileInputStream(path);
            BufferedInputStream bufferedReader = new BufferedInputStream(fis);
            CheckedInputStream checkedInputStream = new CheckedInputStream(bufferedReader, new Adler32());

            while (true) {
                if (checkedInputStream.read(buffer) < 0) {
                    break;
                }
            }

            checkedInputStream.close();
            bufferedReader.close();
            fis.close();

            validationResult = Long.compare(LONG_SERIALIZER.deserialize(sign),
                    checkedInputStream.getChecksum().getValue()) == 0;
        } catch (IOException ignored) {
            validationResult = false;
        }

        return validationResult;
    }
}
