package ru.mipt.java2016.homework.g594.sharuev.task2;

import org.objenesis.ObjenesisStd;

import java.io.*;
import java.lang.reflect.Field;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/*
 * Сериализатор для простых данных.
 * Простыми считаются Integer/int, Long/long, Boolean/boolean, Double/double,
 * String, Date и классы, содержащие поля только из этих типов. Поля предков тоже сериализуются.
 * Подклассов - нет.
 */
public class PODSerializer<Value> implements SerializationStrategy<Value> {

    private Class clazz;

    public PODSerializer(Class clazzVal) {
        clazz = clazzVal;
    }

    public void serializeToStream(Value value,
                                  OutputStream outputStream) throws SerializationException {
        DataOutputStream dos = new DataOutputStream(outputStream);

        try {
            serializePOD(value, dos);
        } catch (UnknownObjectException e) {
            // Это не тип, который сериализует serializePOD. Может, это класс с такими полями?
            //Field[] fields = value.getClass().getDeclaredFields();
            ArrayList<Field> fields = new ArrayList<>();
            Class aClass = clazz;
            do {
                Collections.addAll(fields, aClass.getDeclaredFields());
                aClass = aClass.getSuperclass();
            } while (aClass != null);

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    serializePOD(field.get(value), dos);
                } catch (UnknownObjectException | SerializationException | IllegalAccessException e2) {
                    throw new SerializationException("Can't access member to serialize it", e2);
                }
            }
        }

    }

    private void serializePOD(Object o, DataOutputStream outputStream)
            throws SerializationException, UnknownObjectException {
        try {
            if (o instanceof Integer) {
                outputStream.writeInt(((Integer) o));
            } else if (o instanceof Double) {
                outputStream.writeDouble((Double) o);
            } else if (o instanceof Boolean) {
                outputStream.writeBoolean((Boolean) o);
            } else if (o instanceof String) {
                /*byte[] bytes = ((String)o).getBytes("UTF-8");
                outputStream.writeInt(bytes.length);
                outputStream.write(bytes);*/
                outputStream.writeUTF((String) o);
            } else if (o instanceof Date) {
                //serializePOD((new SimpleDateFormat()).format((Date) o), outputStream);
                outputStream.writeLong(((Date) o).getTime());
            } else {
                throw new UnknownObjectException("Unknown POD type");
            }
        } catch (IOException e) {
            throw new SerializationException("IO POD serialization error", e);
        }
    }

    private Object deserializePOD(Class o, DataInputStream inputStream)
            throws SerializationException, UnknownObjectException {
        try {
            if (o == Integer.class || o == int.class) {
                return inputStream.readInt();
            } else if (o == Double.class || o == double.class) {
                return inputStream.readDouble();
            } else if (o == Boolean.class || o == boolean.class) {
                return inputStream.readBoolean();
            } else if (o == String.class) {
                /*int size = inputStream.readInt();
                byte[] bytes = new byte[size];
                inputStream.readFully(bytes);
                return new String(bytes, "UTF-8");*/
                return inputStream.readUTF();
            } else if (o == Date.class) {
                /*try {
                    return new SimpleDateFormat().parse(inputStream.readUTF());
                } catch (ParseException e) {
                    throw new IOException("Date parse failed");
                }*/
                return new Date(inputStream.readLong());
            } else {
                throw new UnknownObjectException("Unknown POD type");
            }
        } catch (IOException e) {
            throw new SerializationException("IO POD serialization error", e);
        }
    }

    public Value deserializeFromStream(InputStream inputStream) throws SerializationException {
        DataInputStream dis = new DataInputStream(inputStream);
        try {
            return (Value) deserializePOD(clazz, dis);
        } catch (UnknownObjectException e) {
            //Field[] fields = clazz.getDeclaredFields();
            ArrayList<Field> fields = new ArrayList<>();
            Class aClass = clazz;
            do {
                Collections.addAll(fields, aClass.getDeclaredFields());
                aClass = aClass.getSuperclass();
            } while (aClass != null);

            Value ret = (Value) (new ObjenesisStd()).getInstantiatorOf(clazz).newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    field.set(ret, deserializePOD(field.getType(), dis));
                } catch (IllegalAccessException | UnknownObjectException e2) {
                    throw new SerializationException("Can't access member to serialize it", e2);
                }
            }
            return ret;
        }

    }

    //private final static String dateFormat = "yyyy-MM-dd ";
}
