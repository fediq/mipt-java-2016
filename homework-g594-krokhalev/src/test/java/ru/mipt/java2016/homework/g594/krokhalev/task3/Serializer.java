package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

class Serializer<T> implements SerializationStrategy<T> {
    private final Class<T> mClassT;

    Serializer(Class<T> classT) {
        mClassT = classT;
    }

    @Override
    public void serialize(DataOutput dos, Object object) throws IOException {
        Class oClass = object.getClass();
        if (oClass.isArray()) {

            serialize(dos, Array.getLength(object));

            for (int i = 0; i < Array.getLength(object); i++) {
                serialize(dos, Array.get(object, i));
            }

        } else if (object.getClass().equals(boolean.class) || object.getClass().equals(Boolean.class)) {
            dos.writeBoolean((boolean) object);
        } else if (object.getClass().equals(byte.class) || object.getClass().equals(Byte.class)) {
            dos.write(new byte[]{(byte) object});
        } else if (object.getClass().equals(char.class) || object.getClass().equals(Character.class)) {
            dos.writeChar((char) object);
        } else if (object.getClass().equals(short.class) || object.getClass().equals(Short.class)) {
            dos.writeShort((short) object);
        } else if (object.getClass().equals(int.class) || object.getClass().equals(Integer.class)) {
            dos.writeInt((int) object);
        } else if (object.getClass().equals(long.class) || object.getClass().equals(Long.class)) {
            dos.writeLong((long) object);
        } else if (object.getClass().equals(float.class) || object.getClass().equals(Float.class)) {
            dos.writeFloat((float) object);
        } else if (object.getClass().equals(double.class) || object.getClass().equals(Double.class)) {
            dos.writeDouble((double) object);
        } else if (object.getClass().equals(String.class)) {
            dos.writeUTF((String) object);
        } else if (object.getClass().equals(Date.class)) {
            Date dObject = (Date) object;
            serialize(dos, dObject.getTime());
        } else {
            try {
                if (!oClass.getSuperclass().equals(Object.class)) {
                    Field[] fields = oClass.getSuperclass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        serialize(dos, field.get(object));
                    }
                }

                Field[] fields = oClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    serialize(dos, field.get(object));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Object deserialize(Class<?> oClass, DataInput dis) throws IOException {
        Object object = null;
        if (oClass.isArray()) {
            Integer length = dis.readInt();
            object = (Array) Array.newInstance(oClass.getComponentType(), length);
            for (int i = 0; i < length; ++i) {
                Array.set(object, i, deserialize(oClass.getComponentType(), dis));
            }
        } else if (oClass.equals(boolean.class) || oClass.equals(Boolean.class)) {
            object = dis.readBoolean();
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
            object = dis.readUTF();
        } else if (oClass.equals(Date.class)) {
            long time = dis.readLong();

            object = new Date(time);
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

    @Override
    public T deserialize(DataInput dis) throws IOException {
        return (T) deserialize(mClassT, dis);
    }
}