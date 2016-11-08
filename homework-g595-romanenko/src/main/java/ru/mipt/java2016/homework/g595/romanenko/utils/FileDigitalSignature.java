package ru.mipt.java2016.homework.g595.romanenko.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ru.mipt.java2016.homework.g595.romanenko.utils
 *
 * @author Ilya I. Romanenko
 * @since 08.11.16
 **/
public interface FileDigitalSignature {

    default void signFile(String path, String signPath) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(signPath);
        fileOutputStream.write(getFileSign(path));
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    byte[] getFileSign(String path) throws IOException;

    default void signFileWithDefaultSignName(String path) throws IOException {
        signFile(path, path + ".sign");
    }

    boolean validateFileSign(String path, byte[] sign);

    default boolean validateFileSignWithDefaultSignName(String path) {
        return validateFileSign(path, path + ".sign");
    }

    default boolean validateFileSign(String path, String signPath) {
        boolean validationResult;
        try {
            FileInputStream fileInputStream = new FileInputStream(signPath);
            byte[] sign = new byte[fileInputStream.available()];
            fileInputStream.read(sign);
            fileInputStream.close();
            validationResult = validateFileSign(path, sign);
        } catch (IOException ignored) {
            validationResult = false;
        }
        return validationResult;
    }

}
