package ru.mipt.java2016.homework.g595.kireev.task2;


import ru.mipt.java2016.homework.tests.task2.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;



/**
 * Created by Карим on 25.10.2016.
 */
public class MySerializator<T> {
    private final String type;
    private static final Set<String> usableTypes;

    static {
        Set<String> set = new HashSet<>();
        set.add("Integer");
        set.add("Double");
        set.add("String");
        set.add("StudentKey");
        set.add("Student");
        usableTypes = Collections.unmodifiableSet(set);
    }

    MySerializator(String type) {
        if (!usableTypes.contains(type)) {
            throw new RuntimeException("Wrong type");
        }
        this.type = type;
    }

    byte[] serialize(T obj) {
        switch (type) {
            case "String":
                return toBytes((String) obj);
            case "Integer":
                return toBytes((Integer) obj);
            case "Double":
                return toBytes((Double) obj);
            case "StudentKey":
                return toBytes((StudentKey) obj);
            case "Student":
                return toBytes((Student) obj);
            default:
                throw new RuntimeException("Fatal Error : unknown class");
        }
    }

    T deserialize(byte[] bytes) {
        switch (type) {
            case "String":
                return (T) toString(bytes);
            case "Integer":
                return (T) toInteger(bytes);
            case "Double":
                return (T) toDouble(bytes);
            case "StudentKey":
                return (T) toStudentKey(bytes);
            case "Student":
                return (T) toStudent(bytes);
            default:
                throw new RuntimeException("Fatal Error : unknown class");
        }
    }


    private static byte[] toBytes(String str) {
        byte[] strBytes = str.getBytes();
        byte[] len = toBytes(strBytes.length);
        return concatenateArrays(len, strBytes);
    }

    private static String toString(byte[] bytes) {
        if (bytes.length < 4) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            int len = toInteger(subArray(bytes, 0, 4));
            if (bytes.length != len + 4) {
                throw new RuntimeException("Invalid Conversion");
            } else {
                return new String(subArray(bytes, 4, bytes.length));
            }
        }
    }

    private static byte[] toBytes(int val) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(val);
        return bytes;
    }

    static Integer toInteger(byte[] bytes) {
        if (bytes.length != 4) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return ByteBuffer.wrap(bytes).getInt();
        }
    }

    private static byte[] toBytes(Double val) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(val);
        return bytes;
    }

    private static Double toDouble(byte[] bytes) {
        if (bytes.length != 8) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return ByteBuffer.wrap(bytes).getDouble();
        }
    }

    private static byte[] toBytes(boolean b) {
        byte[] ret = new byte[1];
        ret[0] = b ? (byte) 1 : (byte) 0;
        return ret;
    }

    private static boolean toBoolean(byte[] bytes) {
        if (bytes.length != 1) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return bytes[0] == (byte) 1;
        }
    }

    static byte[] toBytes(long val) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(val);
        return bytes;
    }

    static long toLong(byte[] bytes) {
        if (bytes.length != 8) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return ByteBuffer.wrap(bytes).getLong();
        }
    }

    private static byte[] toBytes(Date d) {
        return toBytes(d.getTime());
    }

    private static Date toDate(byte[] bytes) {
        return new Date(toLong(bytes));
    }

    private static byte[] toBytes(StudentKey sk) {
        byte[] groupId = toBytes(sk.getGroupId());
        byte[] name = toBytes(sk.getName());
        return concatenateArrays(groupId, name);
    }

    private static StudentKey toStudentKey(byte[] b) {
        if (b.length < 4) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            byte[] groupId = subArray(b, 0, 4);
            byte[] name = subArray(b, 4, b.length);
            return new StudentKey(toInteger(groupId), toString(name));
        }
    }

    private static byte[] toBytes(Student obj) {
        byte[] groupId = toBytes(obj.getGroupId());
        byte[] name = toBytes(obj.getName());
        byte[] hometown = toBytes(obj.getHometown());
        byte[] birthDate = toBytes(obj.getBirthDate());
        byte[] hasDormitory = toBytes(obj.isHasDormitory());
        byte[] averageScore = toBytes(obj.getAverageScore());

        return concatenateArrays(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    private static Student toStudent(byte[] b) {
        int i = 0;
        int len1;
        int len2;

        byte[] groupId = subArray(b, i, i + 4);
        i = i + 4;

        len1 = 4 + toInteger(subArray(b, i, i + 4));
        byte[] name = subArray(b, i, i + len1);
        i = i + len1;

        len2 = 4 + toInteger(subArray(b, i, i + 4));
        byte[] hometown = subArray(b, i, i + len2);
        i = i + len2;

        byte[] birthDate = subArray(b, i, i + 8);
        i = i + 8;

        byte[] hasDormitory = subArray(b, i, i + 1);
        i = i + 1;

        byte[] averageScore = subArray(b, i, i + 8);
        //i = i + 8;

        return new Student(toInteger(groupId), toString(name), toString(hometown),
                toDate(birthDate), toBoolean(hasDormitory), toDouble(averageScore));
    }

    private static byte[] concatenateArrays(byte[]... arrays) {
        int len = 0;
        for (byte[] array : arrays) {
            len += array.length;
        }
        byte[] ret = new byte[len];
        int retIndex = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, ret, retIndex, array.length);
            retIndex += array.length;
        }
        return ret;
    }

    private static byte[] subArray(byte[] bytes, int l, int r) {
        if ((0 <= l) && (0 <= r) && (l <= bytes.length)
                && (r <= bytes.length) && (l <= r)) {
            byte[] ret = new byte[r - l];
            System.arraycopy(bytes, l, ret, 0, r - l);
            return ret;
        } else {
            throw new RuntimeException("Illegal subArray");
        }
    }

}
