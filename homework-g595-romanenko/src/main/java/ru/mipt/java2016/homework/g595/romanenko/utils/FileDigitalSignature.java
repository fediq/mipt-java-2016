package ru.mipt.java2016.homework.g595.romanenko.utils;

import sun.security.rsa.RSAKeyPairGenerator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

/**
 * RSA digital signature for files
 *
 * @author Ilya I. Romanenko
 * @since 30.10.16
 **/
public class FileDigitalSignature {

    private static final long PI_SEED = 3141592653589793238L; //everyone love Pi =)
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    private static FileDigitalSignature INSTANCE = new FileDigitalSignature();

    public static FileDigitalSignature getInstance() {
        return INSTANCE;
    }

    private FileDigitalSignature() {
        genKeys(PI_SEED);
    }

    private void genKeys(long seed) {
        try {

            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            random.setSeed(seed);

            RSAKeyPairGenerator keyGen = new RSAKeyPairGenerator();
            keyGen.initialize(1024, random);

            KeyPair pair = keyGen.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void signFile(String path, String signPath) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(signPath);
        fileOutputStream.write(getFileSign(path));
        fileOutputStream.flush();
        fileOutputStream.close();

    }

    public void signFileWithDefaultSignName(String path) throws IOException {
        signFile(path, path + ".sign");
    }

    public byte[] getFileSign(String path) throws IOException {
        byte[] sign = null;
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);

            FileInputStream fis = new FileInputStream(path);
            BufferedInputStream bufferedReader = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufferedReader.read(buffer)) >= 0) {
                signature.update(buffer, 0, len);
            }
            bufferedReader.close();
            fis.close();

            sign = signature.sign();

        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return sign;
    }

    public boolean validateFileSign(String path, String signPath) {
        boolean validationResult = false;
        try {
            FileInputStream fileInputStream = new FileInputStream(signPath);
            byte[] sign = new byte[fileInputStream.available()];
            fileInputStream.read(sign);
            fileInputStream.close();
            validationResult =validateFileSign(path, sign);
        } catch (IOException ignored) { }
        return validationResult;
    }

    public boolean validateFileSign(String path, byte[] sign) {
        boolean result = false;

        try {

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);

            FileInputStream file = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) >= 0) {
                signature.update(buffer, 0, len);
            }
            stream.close();
            file.close();

            result = signature.verify(sign);

        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException ignored) { }

        return result;
    }

    public boolean validateFileSignWithDefaultSignName(String path) {
        return validateFileSign(path, path + ".sign");
    }
}
