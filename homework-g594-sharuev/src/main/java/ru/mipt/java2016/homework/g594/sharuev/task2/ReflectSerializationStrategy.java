package ru.mipt.java2016.homework.g594.sharuev.task2;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class ReflectSerializationStrategy<Value> implements SerializationStrategy<Value> {

    public byte[] serializeToBytes(Value value) throws SerializationException {
        byte[] b = serializeToBOS(value).toByteArray();
        return b;
    }

    public void serializeToStream(Value value, OutputStream outputStream) throws SerializationException {
        try {
            outputStream.write(serializeToBytes(value));
        } catch (IOException e) {
            throw new SerializationException("Can't write to your stream", e);
        }
    }

    public Value deserializeFromStream(InputStream inputStream) throws SerializationException {
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(inputStream);
            Class aClass = (Class) in.readObject();
            Value val;
            try {
                for (Constructor constructor: aClass.getDeclaredConstructors()) {
                    constructor.setAccessible(true);
                }
                val = (Value)aClass.newInstance();
            } catch (InstantiationException e) {
                throw new SerializationException("oops 2", e);
            }
            ArrayList<Field> fields = new ArrayList<>();
            do {
                Collections.addAll(fields, aClass.getDeclaredFields());
                aClass = aClass.getSuperclass();
            } while (aClass != null);

            for (Field field : fields) {
                Object fieldValue = in.readObject();
                field.setAccessible(true);
                field.set(val, fieldValue);
            }
            return val;
        } catch (IOException e) {
            throw new SerializationException("Can't read from your stream", e);
        } catch (ClassNotFoundException e) {
            throw new SerializationException("Deserializing unexistent class", e);
        } catch (IllegalAccessException e) {
            throw new SerializationException("oops", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                //throw new SerializationException("Can't close temporary stream", e);
                //or ignore?
            }
        }
    }

    public Value deserialize(byte[] bytes, int offset) throws SerializationException {
        // OFFSET FORGOTTEN!
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return deserializeFromStream(bis);
    }

    /**
     * Сериализация класса.
     * Сериализованное значение пишется побайтово: класс и все его поля, в том числе - рекурсивно поля родителя.
     *
     * @param value Значение, которое нужно сериализовать.
     * @return ByteArrayOutputStream с записанным сериализованным значением.
     * @throws SerializationException Если всё накрылось.
     */
    private ByteArrayOutputStream serializeToBOS(Value value) throws SerializationException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
        } catch (IOException e) {
            throw new SerializationException("Can't open serialization stream", e);
        }
        try {
            oos.writeObject(value.getClass());
        } catch (IOException e) {
            throw new SerializationException("Can't write to serialization stream", e);
        }
        ArrayList<Field> fields = new ArrayList<>();
        Class aClass = value.getClass();
        do {
            Collections.addAll(fields, aClass.getDeclaredFields());
            aClass = aClass.getSuperclass();
        } while (aClass != null);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                oos.writeObject(field.get(value));
            } catch (IllegalAccessException e) {
                throw new SerializationException("Can't access member to serialize it", e);
            } catch (IOException e) {
                throw new SerializationException("Can't write to serialization stream", e);
            }
        }
        return bos;
    }

    //private Type type = Value
}
