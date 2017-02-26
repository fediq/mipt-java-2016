package ru.mipt.java2016.homework.g595.novikov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

public abstract class MySerialization<T> {
    public abstract void serialize(DataOutput file, T object) throws IOException;

    public abstract T deserialize(DataInput file) throws IOException;

    public abstract long getSizeSerialized(T object);

    protected static void serializeString(DataOutput file, String str) throws IOException {
        file.writeUTF(str);
    }

    protected static String deserializeString(DataInput file) throws IOException {
        return file.readUTF();
    }

    protected static void serializeInteger(DataOutput file, Integer object) throws IOException {
        file.writeInt(object.intValue());
    }

    protected static Integer deserializeInteger(DataInput file) throws IOException {
        return file.readInt();
    }

    protected static void serializeDouble(DataOutput file, Double object) throws IOException {
        file.writeDouble(object.doubleValue());
    }

    protected static Double deserializeDouble(DataInput file) throws IOException {
        return file.readDouble();
    }

    protected static void serializeBoolean(DataOutput file, Boolean object) throws IOException {
        file.writeBoolean(object.booleanValue());
    }

    protected static Boolean deserializeBoolean(DataInput file) throws IOException {
        return file.readBoolean();
    }

    protected static void serializeDate(DataOutput file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }

    protected static Date deserializeDate(DataInput file) throws IOException {
        return new Date(file.readLong());
    }

    protected static long getSizeSerializedInteger(Integer object) {
        return Integer.BYTES;
    }

    protected static long getSizeSerializedDouble(Double object) {
        return Double.BYTES;
    }

    protected static long getSizeSerializedString(String object) {
        return Integer.BYTES + object.length(); // this is approximate size
    }

    protected static long getSizeSerializedLong(Long object) {
        return Long.BYTES;
    }

    protected static long getSizeSerializedBoolean(Boolean object) {
        return 1; // ?? I don't know is it true
    }

    protected static long getSizeSerializedDate(Date date) {
        return Long.BYTES;
    }
}
