package ru.mipt.java2016.homework.g595.romanenko.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * ru.mipt.java2016.homework.g595.romanenko.utils
 *
 * @author Ilya I. Romanenko
 * @since 08.11.16
 **/
public class FileDigitalSignatureMD5 implements FileDigitalSignature {

    private static final FileDigitalSignatureMD5 INSTANCE = new FileDigitalSignatureMD5();

    public static FileDigitalSignatureMD5 getInstance() {
        return INSTANCE;
    }

    private FileDigitalSignatureMD5() {

    }

    @Override
    public byte[] getFileSign(String path) throws IOException {
        byte[] sign = null;
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");

            FileInputStream fis = new FileInputStream(path);
            BufferedInputStream bufferedReader = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufferedReader.read(buffer)) >= 0) {
                algorithm.update(buffer, 0, len);
            }
            bufferedReader.close();
            fis.close();
            sign = algorithm.digest();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sign;
    }

    @Override
    public boolean validateFileSign(String path, byte[] sign) {
        boolean validationResult = false;

        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");

            FileInputStream file = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) >= 0) {
                algorithm.update(buffer, 0, len);
            }
            stream.close();
            file.close();

            validationResult = Arrays.equals(sign, algorithm.digest());

        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException ignored) {
            validationResult = false;
        }

        return validationResult;
    }
}
