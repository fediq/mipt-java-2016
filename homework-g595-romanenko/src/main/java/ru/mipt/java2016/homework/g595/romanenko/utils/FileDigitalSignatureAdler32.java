package ru.mipt.java2016.homework.g595.romanenko.utils;

import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.LongSerializer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Adler32;

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


    @Override
    public byte[] getFileSign(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bufferedReader = new BufferedInputStream(fis);
        Adler32 algorithm = new Adler32();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufferedReader.read(buffer)) >= 0) {
            algorithm.update(buffer, 0, len);
        }
        bufferedReader.close();
        fis.close();

        return LONG_SERIALIZER.serializeToBytes(algorithm.getValue());
    }

    @Override
    public boolean validateFileSign(String path, byte[] sign) {
        boolean validationResult;

        try {
            Adler32 algorithm = new Adler32();

            FileInputStream file = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) >= 0) {
                algorithm.update(buffer, 0, len);
            }
            stream.close();
            file.close();

            validationResult = Long.compare(LONG_SERIALIZER.deserialize(sign), algorithm.getValue()) == 0;
        } catch (IOException ignored) {
            validationResult = false;
        }

        return validationResult;
    }
}
