package ru.mipt.java2016.homework.g594.sharuev.task2;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;

/*
 * Сериализатор для простых данных.
 * Простыми считаются Integer, Long, Boolean, Double, String, Date и классы, содержащие поля только из этих типов.
 */
public class PODSerializer<Value> implements SerializationStrategy<Value> {

    private Class clazz;
    PODSerializer(Class clazz_) {clazz = clazz_;}
    
    public void serializeToStream(Value value, OutputStream outputStream) throws SerializationException {
        DataOutputStream dos = new DataOutputStream(outputStream);
        Field[] fields = value.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                serializePOD(field.get(value), dos);
            } catch (IllegalAccessException e) {
                throw new SerializationException("Can't access member to serialize it", e);
            }
        }
    }

    private void serializePOD(Object o, DataOutputStream outputStream) throws SerializationException
    {
        try {
            if (o instanceof Integer) {
                outputStream.writeInt(((Integer) o));
            } else if (o instanceof Double) {
                outputStream.writeDouble((Double) o);
            } else if (o instanceof Boolean) {
                outputStream.writeBoolean((Boolean) o);
            } else if (o instanceof String) {
                outputStream.writeUTF((String) o);
            } else if (o instanceof Date) {
                outputStream.writeUTF(new SimpleDateFormat(dateFormat).format((Date) o));
            } else {
                throw new SerializationException("Unknown POD type");
            }
        }catch (IOException e) {
            throw new SerializationException("IO POD serialization error", e);
        }
    }

    private Object deserializePOD(Object o, DataInputStream inputStream) throws SerializationException
    {
        try {
            if (o instanceof Integer) {
                return inputStream.readInt();
            } else if (o instanceof Double) {
                return inputStream.readDouble();
            } else if (o instanceof Boolean) {
                return inputStream.readBoolean();
            } else if (o instanceof String) {
                return inputStream.readUTF();
            } else if (o instanceof Date) {
                try {
                    return new SimpleDateFormat(dateFormat).parse(inputStream.readUTF());
                } catch (ParseException e) {
                    throw new IOException("Date parse failed");
                }
            } else {
                throw new SerializationException("Unknown POD type");
            }
        }catch (IOException e) {
            throw new SerializationException("IO POD serialization error", e);
        }
    }

    public Value deserializeFromStream(InputStream inputStream) throws SerializationException {
        try {
            DataInputStream dos = new DataInputStream(inputStream);
            Field[] fields = value.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    field.getGenericType() value = deserializePOD(field.get(value), dos);
                } catch (IllegalAccessException e) {
                    throw new SerializationException("Can't access member to serialize it", e);
                }
            }
        } catch ()
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


    final static String dateFormat = "yyyy-MM-dd";
}
