package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Date;

class Serializer {
    private static final int BOOL_SIZE   = 1;
    private static final int BYTE_SIZE   = 1;
    private static final int CHAR_SIZE   = 2;
    private static final int SHORT_SIZE  = 2;
    private static final int INT_SIZE    = 4;
    private static final int LONG_SIZE   = 8;
    private static final int FLOAT_SIZE  = 4;
    private static final int DOUBLE_SIZE = 8;

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

    static byte[] serialize(Object object) throws IOException {
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
                baos.write(sObject.getBytes());
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

                byte[] buff = new byte[length];
                bb.get(buff);

                object = new String(buff);
            } else if (oClass.equals(Date.class)) {
                long time = bb.getLong();

                object = new Date(time);
            }
        } else {
            try {
                Field[] fields = new Field[oClass.getSuperclass().getDeclaredFields().length +
                        oClass.getDeclaredFields().length];
                System.arraycopy(oClass.getSuperclass().getDeclaredFields(), 0, fields,
                        0, oClass.getSuperclass().getDeclaredFields().length);
                System.arraycopy(oClass.getDeclaredFields(), 0, fields,
                        oClass.getSuperclass().getDeclaredFields().length, oClass.getDeclaredFields().length);

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

    private static void read(InputStream stream, byte[] buff) throws IOException {
        int pos = 0;
        while (pos < buff.length) {
            pos += stream.read(buff, pos, buff.length - pos);
        }
    }

    private static byte readByte(InputStream stream) throws IOException {
        byte[] buff = new byte[BYTE_SIZE];
        read(stream, buff);
        ByteBuffer bb = ByteBuffer.wrap(buff);
        return bb.get();
    }

    private static char readChar(InputStream stream) throws IOException {
        byte[] buff = new byte[CHAR_SIZE];
        read(stream, buff);
        ByteBuffer bb = ByteBuffer.wrap(buff);
        return bb.getChar();
    }

    private static int readInt(InputStream stream) throws IOException {
        byte[] buff = new byte[INT_SIZE];
        read(stream, buff);
        ByteBuffer bb = ByteBuffer.wrap(buff);
        return bb.getInt();
    }

    private static long readLong(InputStream stream) throws IOException {
        byte[] buff = new byte[LONG_SIZE];
        read(stream, buff);
        ByteBuffer bb = ByteBuffer.wrap(buff);
        return bb.getLong();
    }

    private static short readShort(InputStream stream) throws IOException {
        byte[] buff = new byte[SHORT_SIZE];
        read(stream, buff);
        ByteBuffer bb = ByteBuffer.wrap(buff);
        return bb.getShort();
    }

    private static float readFloat(InputStream stream) throws IOException {
        byte[] buff = new byte[FLOAT_SIZE];
        read(stream, buff);
        ByteBuffer bb = ByteBuffer.wrap(buff);
        return bb.getFloat();
    }

    private static double readDouble(InputStream stream) throws IOException {
        byte[] buff = new byte[DOUBLE_SIZE];
        read(stream, buff);
        ByteBuffer bb = ByteBuffer.wrap(buff);
        return bb.getLong();
    }

    public static Object deserialize(Class<?> oClass, DataInputStream dis) throws IOException {
        Object object = null;
        if (oClass.isArray()) {
            Integer length = dis.readInt();
            object = (Array) Array.newInstance(oClass.getComponentType(), length);
            for (int i = 0; i < length; ++i) {
                Array.set(object, i, deserialize(oClass.getComponentType(), dis));
            }
        } else if (isPrimitive(oClass)) {
            if (oClass.equals(boolean.class) || oClass.equals(Boolean.class)) {
                object = (dis.readByte() == 1);
            } else if (oClass.equals(byte.class) || oClass.equals(Byte.class)) {
                object = dis.readByte();
            } else if (oClass.equals(char.class) || oClass.equals(Character.class)) {
                object = dis.readChar();
            } else if (oClass.equals(short.class) || oClass.equals(Short.class)) {
                object = dis.readShort();
            } else if (oClass.equals(int.class) || oClass.equals(Integer.class)) {
                object = dis.readInt();
            } else if (oClass.equals(long.class) || oClass.equals(Long.class)) {
                object = dis.readLong();
            } else if (oClass.equals(float.class) || oClass.equals(Float.class)) {
                object = dis.readFloat();
            } else if (oClass.equals(double.class) || oClass.equals(Double.class)) {
                object = dis.readDouble();
            } else if (oClass.equals(String.class)) {
                int length = dis.readInt();

                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[8096];
                int readed = 0;
                while (length > 0) {
                    readed = dis.read(buffer, 0, Math.min(buffer.length, length));
                    length -= readed;
                    result.write(buffer, 0, readed);
                }

                object = result.toString();
            } else if (oClass.equals(Date.class)) {
                long time = dis.readLong();

                object = new Date(time);
            }
        } else {
            try {
                Field[] fields = new Field[oClass.getSuperclass().getDeclaredFields().length +
                        oClass.getDeclaredFields().length];
                System.arraycopy(oClass.getSuperclass().getDeclaredFields(), 0, fields,
                        0, oClass.getSuperclass().getDeclaredFields().length);
                System.arraycopy(oClass.getDeclaredFields(), 0, fields,
                        oClass.getSuperclass().getDeclaredFields().length, oClass.getDeclaredFields().length);

                Object[] params = new Object[fields.length];
                for (int i = 0; i < fields.length; ++i) {
                    params[i] = deserialize(fields[i].getType(), dis);
                }
                Constructor<?>[] constructors = oClass.getConstructors();
                object = constructors[0].newInstance(params);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static Object deserialize(Class<?> oClass, InputStream stream) throws IOException {
        return deserialize(oClass, new DataInputStream(stream));
    }
}
