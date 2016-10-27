package ru.mipt.java2016.homework.g594.krokhalev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.*;
import java.nio.*;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class KrokhalevsKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String STORAGE_NAME = "storage.db";

    private File storageFile;
    private String storagePath;

    private Class<K> keyClass;
    private Class<V> valueClass;

    KrokhalevsKeyValueStorage (String workDirectoryName, Class<K> keyClass, Class<V> valueClass) {
        super();

        this.keyClass = keyClass;
        this.valueClass = valueClass;

        File workDirectory = new File(workDirectoryName);
        storageFile = null;

        if (workDirectory.exists() && workDirectory.isDirectory()) {
            for (File item : workDirectory.listFiles()) {
                if (item.isFile() && item.getName().equals(STORAGE_NAME)) {
                    storageFile = item;
                    break;
                }
            }

            if (storageFile == null) {
                storagePath = workDirectory.getAbsolutePath() + "/" + STORAGE_NAME;
                storageFile = new File(storagePath);
                try
                {
                    boolean created = storageFile.createNewFile();
                    if (!created) {
                        System.out.println("Can't create file");
                    }
                } catch(IOException ex){

                    System.out.println(ex.getMessage());
                }
            }
        } else {
            System.out.println("Wrong directory");
        }
    }

    private static class Serializer {
        private static boolean isPrimitive(Class c) {
            return c.isPrimitive() ||
                    c.equals(Boolean.class) ||
                    c.equals(Byte.class) ||
                    c.equals(Character.class) ||
                    c.equals(Short.class) ||
                    c.equals(Integer.class) ||
                    c.equals(Long.class) ||
                    c.equals(String.class);
        }

        private static StringBuffer sbSerialize(Object object) {
            StringBuffer sb = new StringBuffer();
            Class oClass = object.getClass();
            if (oClass.isArray()) {

                sb.append(sbSerialize(Array.getLength(object)));
                for (int i = 0; i < Array.getLength(object); i++) {
                    sb.append(sbSerialize(Array.get(object, i)));
                }

            } else if (isPrimitive(oClass)) {
                if (object.getClass().equals(boolean.class) || object.getClass().equals(Boolean.class)) {
                    sb.append((char)object);
                } else if (object.getClass().equals(byte.class) || object.getClass().equals(Byte.class)) {
                    sb.append((char)((byte)object & 0xFF));
                } else if (object.getClass().equals(char.class) || object.getClass().equals(Character.class)) {
                    sb.append(object);
                } else if (object.getClass().equals(short.class) || object.getClass().equals(Short.class)) {
                    char[] tmp = new char[2];
                    short sObject = (short)object;
                    for (int i = 0; i < 2; ++i) {
                        tmp[i] = (char)sObject;
                        sObject >>= 8;
                    }
                    sb.append(tmp);
                } else if (object.getClass().equals(int.class) || object.getClass().equals(Integer.class)) {
                    char[] tmp = new char[4];
                    int iObject = (int)object;
                    for (int i = 0; i < 4; ++i) {
                        tmp[i] = (char)iObject;
                        iObject >>= 8;
                    }
                    sb.append(tmp);
                } else if (object.getClass().equals(long.class) || object.getClass().equals(Long.class)) {
                    char[] tmp = new char[8];
                    long lObject = (int)object;
                    for (int i = 0; i < 8; ++i) {
                        tmp[i] = (char)lObject;
                        lObject >>= 8;
                    }
                    sb.append(tmp);
                } else if (object.getClass().equals(String.class)) {
                    String sObject = (String) object;
                    sb.append(sbSerialize(sObject.length()));
                    for (int i = 0; i < sObject.length(); i++) {
                        sb.append(sbSerialize(sObject.charAt(i)));
                    }
                }
            } else {
                try {
                    Field[] fields = oClass.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAccessible()) {
                            sb.append(sbSerialize(field.get(object)));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return sb;
        }

        static byte[] serialize(Object object) {
            StringBuffer sb = sbSerialize(object);

            return String.valueOf(sb).getBytes();
        }

        private static Object deserialize(Class<?> oClass, char[] buffer, AtomicInteger pos) {
            Object object = null;
            if (oClass.isArray()) {
                Integer length = (Integer) deserialize(Integer.class, buffer, pos);
                object = (Array) Array.newInstance(oClass.getComponentType(), length);
                for (int i = 0; i < length; ++i) {
                    Array.set(object, i, deserialize(oClass.getComponentType(), buffer, pos));
                }
            } else if (isPrimitive(oClass)) {
                if (oClass.equals(boolean.class) || oClass.equals(Boolean.class)) {
                    object = buffer[pos.intValue()];
                    pos.set(pos.get() + 1);
                } else if (oClass.equals(byte.class) || oClass.equals(Byte.class)) {
                    object = (byte)((char)buffer[pos.intValue()] & 0xFF);
                    pos.set(pos.get() + 1);
                } else if (oClass.equals(char.class) || oClass.equals(Character.class)) {
                    object = buffer[pos.intValue()];
                    pos.set(pos.get() + 1);
                } else if (oClass.equals(short.class) || oClass.equals(Short.class)) {
                    short sObject = 0;
                    for (int i = pos.intValue() + 1; i >= pos.intValue(); --i) {
                        sObject <<= 8;
                        sObject += (short) buffer[i];
                    }
                    object = sObject;
                    pos.set(pos.get() + 2);
                } else if (oClass.equals(int.class) || oClass.equals(Integer.class)) {
                    int iObject = 0;
                    for (int i = pos.intValue() + 3; i >= pos.intValue(); --i) {
                        iObject <<= 8;
                        iObject += (short) buffer[i];
                    }
                    object = iObject;
                    pos.set(pos.get() + 4);
                } else if (oClass.equals(long.class) || oClass.equals(Long.class)) {
                    long lObject = 0;
                    for (int i = pos.intValue() + 7; i >= pos.intValue(); --i) {
                        lObject <<= 8;
                        lObject += (short) buffer[i];
                    }
                    object = lObject;
                    pos.set(pos.get() + 8);
                } else if (oClass.equals(String.class)) {
                    int length = (int) deserialize(int.class, buffer, pos);

                    StringBuilder sbObject = new StringBuilder();
                    for (int i = 0; i < length; ++i) {
                        sbObject.append((char) deserialize(char.class, buffer, pos));
                    }

                    object = sbObject.toString();
                }
            } else {
                try {
                    object = oClass.newInstance();
                    Field[] fields = oClass.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAccessible()) {
                            field.set(object, deserialize(field.getClass(), buffer, pos));
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return object;
        }

        static Object deserialize(Class<?> oClass, byte[] buffer) {
            char[] charBuffer = new char[buffer.length];
            for(int i = 0; i < buffer.length;i++){
                charBuffer[i]=(char)(buffer[i] & 0xFF);
            }
            return deserialize(oClass, charBuffer, new AtomicInteger(0));
        }
    }

    private byte readFlag(RandomAccessFile file) throws IOException {
        byte[] flagBuff = new byte[1];
        file.read(flagBuff);

        return (byte) Serializer.deserialize(byte.class, flagBuff);
    }

    private K readKey(RandomAccessFile file) throws IOException {
        byte[] sizeBuff = new byte[4];
        file.read(sizeBuff);
        int size = (int) Serializer.deserialize(int.class, sizeBuff);

        byte[] keyBuff = new byte[size];
        file.read(keyBuff);

        return (K) Serializer.deserialize(keyClass, keyBuff);
    }

    private V readValue(RandomAccessFile file) throws IOException {
        byte[] sizeBuff = new byte[4];
        file.read(sizeBuff);
        int size = (int) Serializer.deserialize(int.class, sizeBuff);

        byte[] valueBuff = new byte[size];
        file.read(valueBuff);

        return (V) Serializer.deserialize(valueClass, valueBuff);
    }

    private void missNext(RandomAccessFile file) throws IOException {
        byte[] sizeBuff = new byte[4];
        file.read(sizeBuff);
        int size = (int) Serializer.deserialize(int.class, sizeBuff);

        file.seek(size);
    }

    @Override
    public V read(K key) {
        try {
            RandomAccessFile file = new RandomAccessFile(storageFile, "rw");

            while (file.getFilePointer() < file.length()) {
                if (readFlag(file) == 0) {
                    if (key.equals(readKey(file))) {
                        return readValue(file);
                    } else {
                        missNext(file);
                    }
                } else {
                    missNext(file);
                    missNext(file);
                }
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean exists(K key) {
        try {
            RandomAccessFile file = new RandomAccessFile(storageFile, "rw");

            while (file.getFilePointer() < file.length()) {
                if (readFlag(file) == 0) {
                    if (key.equals(readKey(file))) {
                        return true;
                    }
                } else {
                    missNext(file);
                    missNext(file);
                }
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void write(K key, V value) {
        try {
            RandomAccessFile file = new RandomAccessFile(storageFile, "rw");
            file.seek(file.length());

            byte[] flag = new byte[1];
            byte[] valueBuff = null;

            flag[0] = 0;
            file.write(flag);

            valueBuff = Serializer.serialize(key);

            file.write(Serializer.serialize(valueBuff.length));
            file.write(valueBuff);

            valueBuff = Serializer.serialize(value);

            file.write(Serializer.serialize(valueBuff.length));
            file.write(valueBuff);

            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) {
        try {
            RandomAccessFile file = new RandomAccessFile(storageFile, "rw");

            while (file.getFilePointer() < file.length()) {
                if (readFlag(file) == 0) {
                    byte[] sizeBuff = new byte[4];
                    file.read(sizeBuff);
                    int size = (int) Serializer.deserialize(int.class, sizeBuff);

                    byte[] keyBuff = new byte[size];
                    file.read(keyBuff);

                    K currKey = (K) Serializer.deserialize(keyClass, keyBuff);

                    if (key.equals(currKey)) {
                        file.seek(file.getFilePointer() - size - 1);

                        byte[] flagBuff = new byte[1];
                        flagBuff[1] = 1;

                        file.write(flagBuff);

                        return;
                    } else {
                        missNext(file);
                    }

                } else {
                    missNext(file);
                    missNext(file);
                }
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<K> readKeys() {

        return null;
    }

    @Override
    public int size() {
        int ans = 0;

        try {
            RandomAccessFile file = new RandomAccessFile(storageFile, "rw");

            while (file.getFilePointer() < file.length()) {
                if (readFlag(file) == 0) {
                    ans++;
                }

                missNext(file);
                missNext(file);
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ans;
    }

    @Override
    public void close() throws IOException {

    }
}
