package ru.mipt.java2016.homework.g594.krokhalev.task2;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.time.Instant;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class KrokhalevsKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String STORAGE_NAME = "storage.db";

    private int version = 0;

    private File storageFile;
    private File keysFile;
    private File valuesFile;

    private Class<K> keyClass;
    private Class<V> valueClass;

    KrokhalevsKeyValueStorage(String workDirectoryName, Class<K> keyClass, Class<V> valueClass) {
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

            try {

                keysFile = new File(workDirectory.getAbsolutePath() + "/KEYS" + STORAGE_NAME);
                valuesFile = new File(workDirectory.getAbsolutePath() + "/VALUES" + STORAGE_NAME);

                if (!keysFile.createNewFile() || !valuesFile.createNewFile()) {
                    throw new IOException();
                }

                if (storageFile == null) {
                    storageFile = new File(workDirectory.getAbsolutePath() + "/" + STORAGE_NAME);

                    if (!storageFile.createNewFile()) {
                        throw new IOException();
                    }

                } else {
                    restoreFromStorage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Wrong directory");
        }
    }

    private static class Serializer {
        private static char[] doubleToChars(double value) {
            byte[] bytes = new byte[8];
            ByteBuffer.wrap(bytes).putDouble(value);
            char[] chars = new char[8];
            for (int i = 0; i < 8; ++i) {
                chars[i] = (char) (bytes[i] & 0xFF);
            }
            return chars;
        }

        private static double charsToDouble(char[] chars) {
            byte[] bytes = new byte[chars.length];
            for (int i = 0; i < chars.length; ++i) {
                bytes[i] = (byte) (chars[i] & 0xFF);
            }
            return ByteBuffer.wrap(bytes).getDouble();
        }


        private static boolean isPrimitive(Class c) {
            return c.isPrimitive() ||
                    c.equals(Boolean.class) ||
                    c.equals(Byte.class) ||
                    c.equals(Character.class) ||
                    c.equals(Short.class) ||
                    c.equals(Integer.class) ||
                    c.equals(Long.class) ||
                    c.equals(Float.class) ||
                    c.equals(Double.class) ||
                    c.equals(Date.class) ||
                    c.equals(String.class);
        }

        private static byte[] serialize(Object object) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Class oClass = object.getClass();
            if (oClass.isArray()) {

                baos.write(serialize(Array.getLength(object)));

                for (int i = 0; i < Array.getLength(object); i++) {
                    baos.write(serialize(Array.get(object, i)));
                }

            } else if (isPrimitive(oClass)) {
                if (object.getClass().equals(boolean.class) || object.getClass().equals(Boolean.class)) {
                    boolean bObject = (boolean) object;
                    if (bObject) {
                        baos.write(new byte[]{1});
                    } else {
                        baos.write(new byte[]{0});
                    }
                } else if (object.getClass().equals(byte.class) || object.getClass().equals(Byte.class)) {
                    baos.write(new byte[]{(byte) object});
                } else if (object.getClass().equals(char.class) || object.getClass().equals(Character.class)) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.putChar((char) object);
                    baos.write(bb.array());
                } else if (object.getClass().equals(short.class) || object.getClass().equals(Short.class)) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.putShort((short) object);
                    baos.write(bb.array());
                } else if (object.getClass().equals(int.class) || object.getClass().equals(Integer.class)) {
                    ByteBuffer bb = ByteBuffer.allocate(4);
                    bb.putInt((int) object);
                    baos.write(bb.array());
                } else if (object.getClass().equals(long.class) || object.getClass().equals(Long.class)) {
                    ByteBuffer bb = ByteBuffer.allocate(8);
                    bb.putLong((long) object);
                    baos.write(bb.array());
                } else if (object.getClass().equals(float.class) || object.getClass().equals(Float.class)) {
                    ByteBuffer bb = ByteBuffer.allocate(4);
                    bb.putFloat((float) object);
                    baos.write(bb.array());
                } else if (object.getClass().equals(double.class) || object.getClass().equals(Double.class)) {
                    ByteBuffer bb = ByteBuffer.allocate(8);
                    bb.putDouble((double) object);
                    baos.write(bb.array());
                } else if (object.getClass().equals(String.class)) {
                    String sObject = (String) object;
                    baos.write(serialize(sObject.length()));
                    for (int i = 0; i < sObject.length(); ++i) {
                        baos.write(serialize(sObject.charAt(i)));
                    }
                } else if (object.getClass().equals(Date.class)) {
                    Date dObject = (Date) object;
                    baos.write(serialize(dObject.getTime()));
                }
            } else {
                try {
                    if (!oClass.getSuperclass().equals(Object.class)) {
                        Field[] fields = oClass.getSuperclass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            baos.write(serialize(field.get(object)));
                        }
                    }

                    Field[] fields = oClass.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        baos.write(serialize(field.get(object)));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return baos.toByteArray();
        }

        private static Object deserialize(Class<?> oClass, ByteBuffer bb) {
            Object object = null;
            if (oClass.isArray()) {
                Integer length = bb.getInt();
                object = (Array) Array.newInstance(oClass.getComponentType(), length);
                for (int i = 0; i < length; ++i) {
                    Array.set(object, i, deserialize(oClass.getComponentType(), bb));
                }
            } else if (isPrimitive(oClass)) {
                if (oClass.equals(boolean.class) || oClass.equals(Boolean.class)) {
                    object = (bb.get() == 1);
                } else if (oClass.equals(byte.class) || oClass.equals(Byte.class)) {
                    object = bb.get();
                } else if (oClass.equals(char.class) || oClass.equals(Character.class)) {
                    object = bb.getChar();
                } else if (oClass.equals(short.class) || oClass.equals(Short.class)) {
                    object = bb.getShort();
                } else if (oClass.equals(int.class) || oClass.equals(Integer.class)) {
                    object = bb.getInt();
                } else if (oClass.equals(long.class) || oClass.equals(Long.class)) {
                    object = bb.getLong();
                } else if (oClass.equals(float.class) || oClass.equals(Float.class)) {
                    object = bb.getFloat();
                } else if (oClass.equals(double.class) || oClass.equals(Double.class)) {
                    object = bb.getDouble();
                } else if (oClass.equals(String.class)) {
                    int length = bb.getInt();

                    StringBuilder sbObject = new StringBuilder();
                    for (int i = 0; i < length; ++i) {
                        sbObject.append(bb.getChar());
                    }

                    object = sbObject.toString();
                } else if (oClass.equals(Date.class)) {
                    long time = bb.getLong();

                    object = new Date(time);
                }
            } else {
                try {
                    Field[] fields = new Field[oClass.getSuperclass().getDeclaredFields().length +
                            oClass.getDeclaredFields().length];
                    System.arraycopy(oClass.getSuperclass().getDeclaredFields(), 0,
                            fields, 0, oClass.getSuperclass().getDeclaredFields().length);
                    System.arraycopy(oClass.getDeclaredFields(), 0,
                            fields, oClass.getSuperclass().getDeclaredFields().length, oClass.getDeclaredFields().length);

                    Object[] params = new Object[fields.length];
                    for (int i = 0; i < fields.length; ++i) {
                        params[i] = deserialize(fields[i].getType(), bb);
                    }
                    Constructor<?>[] constructors = oClass.getConstructors();
                    object = constructors[0].newInstance(params);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return object;
        }

        static Object deserialize(Class<?> oClass, byte[] buffer) {
            return deserialize(oClass, ByteBuffer.wrap(buffer));
        }
    }

    private boolean readBool(RandomAccessFile file) throws IOException {
        byte[] boolBuff = new byte[1];
        file.read(boolBuff);
        return (boolean) Serializer.deserialize(boolean.class, boolBuff);
    }

    private byte[] readBytes(RandomAccessFile file) throws IOException {
        int size = readInt(file);

        byte[] keyBuff = new byte[size];
        file.read(keyBuff);

        return keyBuff;
    }

    private int readInt(RandomAccessFile file) throws IOException {
        byte[] intBuff = new byte[4];
        file.read(intBuff);
        return (int) Serializer.deserialize(int.class, intBuff);
    }

    private long readLong(RandomAccessFile file) throws IOException {
        byte[] longBuff = new byte[8];
        file.read(longBuff);
        return (long) Serializer.deserialize(long.class, longBuff);
    }

    private K readKey(RandomAccessFile file) throws IOException {
        return (K) Serializer.deserialize(keyClass, readBytes(file));
    }

    private V readValue(RandomAccessFile file) throws IOException {
        return (V) Serializer.deserialize(valueClass, readBytes(file));
    }

    private void missNext(RandomAccessFile file) throws IOException {
        int size = readInt(file);

        file.seek(file.getFilePointer() + size);
    }

    private void findKey(RandomAccessFile file, K key) throws IOException {
        while (file.getFilePointer() < file.length()) {
            file.seek(file.getFilePointer() + 1);

            int size = readInt(file);

            byte[] keyBuff = new byte[size];
            file.read(keyBuff);

            K currKey = (K) Serializer.deserialize(keyClass, keyBuff);

            if (key.equals(currKey)) {
                file.seek(file.getFilePointer() - size - 5);
                return;
            } else {
                file.seek(file.getFilePointer() + 8);
            }
        }
    }

    @Override
    public V read(K key) throws Exception {
        if (!keysFile.exists()) throw new Exception();

        V ans = null;
        try {
            RandomAccessFile keysRAFile = new RandomAccessFile(keysFile, "r");
            RandomAccessFile valuesRAFile = new RandomAccessFile(valuesFile, "r");

            findKey(keysRAFile, key);

            if (keysRAFile.getFilePointer() < keysRAFile.length()) {
                keysRAFile.seek(keysRAFile.getFilePointer() + 1);
                missNext(keysRAFile);

                long pos = readLong(keysRAFile);

                valuesRAFile.seek(pos);

                ans = readValue(valuesRAFile);
            }

            keysRAFile.close();
            valuesRAFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    @Override
    public boolean exists(K key) throws Exception {
        if (!keysFile.exists()) throw new Exception();

        boolean ans = false;
        try {
            RandomAccessFile file = new RandomAccessFile(keysFile, "r");

            findKey(file, key);

            if (file.getFilePointer() < file.length()) {
                ans = !readBool(file);
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    @Override
    public void write(K key, V value) throws Exception {
        if (!keysFile.exists()) throw new Exception();

        try {
            RandomAccessFile keysRAFile = new RandomAccessFile(keysFile, "rw");
            RandomAccessFile valuesRAFile = new RandomAccessFile(valuesFile, "rw");
            valuesRAFile.seek(valuesRAFile.length());

            findKey(keysRAFile, key);
            keysRAFile.write(Serializer.serialize(false));
            if (keysRAFile.getFilePointer() < keysRAFile.length()) {

                missNext(keysRAFile);

            } else {
                byte[] keyBuff = null;

                keyBuff = Serializer.serialize(key);

                keysRAFile.write(Serializer.serialize(keyBuff.length));
                keysRAFile.write(keyBuff);

                version++;

            }
            keysRAFile.write(Serializer.serialize(valuesRAFile.getFilePointer()));

            byte[] valueBuff = null;

            valueBuff = Serializer.serialize(value);

            valuesRAFile.write(Serializer.serialize(valueBuff.length));
            valuesRAFile.write(valueBuff);

            keysRAFile.close();
            valuesRAFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) throws Exception {
        if (!keysFile.exists()) throw new Exception();

        try {
            RandomAccessFile file = new RandomAccessFile(keysFile, "rw");

            findKey(file, key);
            if (file.getFilePointer() < file.length()) {
                version++;

                file.write(Serializer.serialize(true));
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<K> readKeys() throws Exception {
        if (!keysFile.exists()) throw new Exception();

        try {
            return new Iterator<K>() {
                long pos = 0;
                int iterVersion = version;

                private boolean hasNext(RandomAccessFile file) {
                    try {
                        while (file.getFilePointer() < file.length()) {
                            boolean isDel = readBool(file);
                            if (isDel) {
                                missNext(file);
                                file.seek(file.getFilePointer() + 8);
                            } else {
                                file.seek(file.getFilePointer() - 1);
                                break;
                            }
                        }

                        return file.getFilePointer() < file.length();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean hasNext() {
                    if (version != iterVersion) throw new ConcurrentModificationException();

                    boolean ans = false;
                    try {
                        RandomAccessFile file = new RandomAccessFile(keysFile, "r");
                        file.seek(pos);

                        ans = hasNext(file);
                        pos = file.getFilePointer();

                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return ans;
                }

                @Override
                public K next() {
                    try {
                        RandomAccessFile file = new RandomAccessFile(keysFile, "r");
                        file.seek(pos);

                        if (hasNext(file)) {
                            readBool(file);
                            K key = readKey(file);
                            file.seek(file.getFilePointer() + 8);

                            pos = file.getFilePointer();
                            return key;
                        }

                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int size() throws Exception {
        if (!keysFile.exists()) throw new Exception();

        int ans = 0;

        try {
            RandomAccessFile file = new RandomAccessFile(keysFile, "r");

            while (file.getFilePointer() < file.length()) {
                boolean isDel = readBool(file);
                if (!isDel) {
                    ans++;
                }

                missNext(file);
                file.seek(file.getFilePointer() + 8);
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ans;
    }

    private void restoreFromStorage() throws IOException {
        RandomAccessFile storageRAFile = new RandomAccessFile(storageFile, "r");
        RandomAccessFile keysRAFile    = new RandomAccessFile(keysFile, "rw");
        RandomAccessFile valuesRAFile  = new RandomAccessFile(valuesFile, "rw");

        while (storageRAFile.getFilePointer() < storageRAFile.length()) {
            byte[] tmp = readBytes(storageRAFile);
            keysRAFile.write(Serializer.serialize(false));
            keysRAFile.write(Serializer.serialize(tmp.length));
            keysRAFile.write(tmp);
            keysRAFile.write(Serializer.serialize(valuesRAFile.getFilePointer()));

            tmp = readBytes(storageRAFile);
            valuesRAFile.write(Serializer.serialize(tmp.length));
            valuesRAFile.write(tmp);
        }

        storageRAFile.close();
        keysRAFile.close();
        valuesRAFile.close();
    }

    private void saveToStorage() throws IOException {
        if (storageFile.delete()) {
            if (!storageFile.createNewFile()) {
                throw new IOException();
            }

            RandomAccessFile storageRAFile = new RandomAccessFile(storageFile, "rw");
            RandomAccessFile keysRAFile = new RandomAccessFile(keysFile, "r");
            RandomAccessFile valuesRAFile = new RandomAccessFile(valuesFile, "r");

            while (keysRAFile.getFilePointer() < keysRAFile.length()) {
                boolean isDel = readBool(keysRAFile);

                if (!isDel) {
                    byte[] tmp = readBytes(keysRAFile);
                    storageRAFile.write(Serializer.serialize(tmp.length));
                    storageRAFile.write(tmp);

                    long pos = readLong(keysRAFile);

                    valuesRAFile.seek(pos);
                    tmp = readBytes(valuesRAFile);
                    storageRAFile.write(Serializer.serialize(tmp.length));
                    storageRAFile.write(tmp);
                } else {
                    missNext(keysRAFile);
                    keysRAFile.seek(keysRAFile.getFilePointer() + 8);
                }
            }

            keysRAFile.close();
            valuesRAFile.close();
            storageRAFile.close();

        } else {
            throw new IOException();
        }
    }

    @Override
    public void close() throws IOException {
        saveToStorage();

        if (!keysFile.delete() || !valuesFile.delete()) {
            throw new IOException();
        }
    }
}
