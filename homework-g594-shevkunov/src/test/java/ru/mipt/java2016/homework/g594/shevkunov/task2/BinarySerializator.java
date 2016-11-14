package ru.mipt.java2016.homework.g594.shevkunov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * (De)Serialializator class
 * Created by shevkunov on 22.10.16.
 */
public class BinarySerializator<K> implements LazyMergedKeyValueStorageSerializator<K> {
    private final String type;
    private static final Set<String> ALLOWED_TYPES;

    static {
        Set<String> set = new HashSet<>();
        set.add("String");
        set.add("Integer");
        set.add("Double");
        set.add("StudentKey");
        set.add("Student");
        ALLOWED_TYPES = Collections.unmodifiableSet(set);
    }

    public BinarySerializator(String type) {
        if (!ALLOWED_TYPES.contains(type)) {
            throw new RuntimeException("Bad argument");
        }
        this.type = type;
    }

    @Override
    public String name() {
        return type;
    }

    public byte[] serialize(K obj) {
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

    public K deSerialize(byte[] bytes) {
        switch (type) {
            case "String":
                return (K) toString(bytes);
            case "Integer":
                return (K) toInteger(bytes);
            case "Double":
                return (K) toDouble(bytes);
            case "StudentKey":
                return (K) toStudentKey(bytes);
            case "Student":
                return (K) toStudent(bytes);
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

    private static Integer toInteger(byte[] bytes) {
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

    public byte[] toBytes(long val) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(val);
        return bytes;
    }

    public long toLong(byte[] bytes) {
        if (bytes.length != 8) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return ByteBuffer.wrap(bytes).getLong();
        }
    }

    private byte[] toBytes(Date d) {
        return toBytes(d.getTime());
    }

    private Date toDate(byte[] bytes) {
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

    private byte[] toBytes(Student obj) {
        byte[] groupId = toBytes(obj.getGroupId());
        byte[] name = toBytes(obj.getName());
        byte[] hometown = toBytes(obj.getHometown());
        byte[] birthDate = toBytes(obj.getBirthDate());
        byte[] hasDormitory = toBytes(obj.isHasDormitory());
        byte[] averageScore = toBytes(obj.getAverageScore());

        return concatenateArrays(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    private Student toStudent(byte[] b) {
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
