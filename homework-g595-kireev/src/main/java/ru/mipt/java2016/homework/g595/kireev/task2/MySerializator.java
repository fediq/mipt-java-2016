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
    private static final Set<String> USABLE_TYPES;

    static {
        Set<String> set = new HashSet<>();
        set.add("Integer");
        set.add("Double");
        set.add("String");
        set.add("StudentKey");
        set.add("Student");
        USABLE_TYPES = Collections.unmodifiableSet(set);
    }

    MySerializator(String type) {
        if (!USABLE_TYPES.contains(type)) {
            throw new RuntimeException("Wrong type");
        }
        this.type = type;
    }

    byte[] serialize(T obj) {
        switch (type) {
            case "String":
                return toByteArray((String) obj);
            case "Integer":
                return toByteArray((Integer) obj);
            case "Double":
                return toByteArray((Double) obj);
            case "StudentKey":
                return toByteArray((StudentKey) obj);
            case "Student":
                return toByteArray((Student) obj);
            default:
                throw new RuntimeException("Fatal Error : unknown class");
        }
    }

    T deserialize(byte[] bytes) {
        switch (type) {
            case "String":
                return (T) bytesToString(bytes);
            case "Integer":
                return (T) bytesToInteger(bytes);
            case "Double":
                return (T) bytesToDouble(bytes);
            case "StudentKey":
                return (T) bytesToStudentKey(bytes);
            case "Student":
                return (T) bytesToStudent(bytes);
            default:
                throw new RuntimeException("Fatal Error : unknown class");
        }
    }

    static Integer bytesToInteger(byte[] bytes) {
        if (bytes.length != 4) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return ByteBuffer.wrap(bytes).getInt();
        }
    }

    private static byte[] toByteArray(int val) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(val);
        return bytes;
    }

    static long bytesToLong(byte[] bytes) {
        if (bytes.length != 8) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return ByteBuffer.wrap(bytes).getLong();
        }
    }

    static byte[] toByteArray(long val) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(val);
        return bytes;
    }
    
    private static Double bytesToDouble(byte[] bytes) {
        if (bytes.length != 8) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return ByteBuffer.wrap(bytes).getDouble();
        }
    }

    private static byte[] toByteArray(Double val) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(val);
        return bytes;
    }

    private static String bytesToString(byte[] bytes) {
        if (bytes.length < 4) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            int len = bytesToInteger(subArray(bytes, 0, 4));
            if (bytes.length != len + 4) {
                throw new RuntimeException("Invalid Conversion");
            } else {
                return new String(subArray(bytes, 4, bytes.length));
            }
        }
    }

    private static byte[] toByteArray(String str) {
        byte[] strBytes = str.getBytes();
        byte[] len = toByteArray(strBytes.length);
        return arraysSum(len, strBytes);
    }

    private static boolean bytesToBoolean(byte[] bytes) {
        if (bytes.length != 1) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            return bytes[0] == (byte) 1;
        }
    }

    private static byte[] toByteArray(boolean b) {
        byte[] ret = new byte[1];
        ret[0] = b ? (byte) 1 : (byte) 0;
        return ret;
    }

    private static Date bytesToDate(byte[] bytes) {
        return new Date(bytesToLong(bytes));
    }

    private static byte[] toByteArray(Date d) {
        return toByteArray(d.getTime());
    }

    private static byte[] toByteArray(StudentKey tmp) {
        byte[] groupId = toByteArray(tmp.getGroupId());
        byte[] name = toByteArray(tmp.getName());
        return arraysSum(groupId, name);
    }

    private static StudentKey bytesToStudentKey(byte[] b) {
        if (b.length < 8) { //4 for group and 4 for string length
            throw new RuntimeException("Invalid Conversion");
        } else {
            byte[] groupId = subArray(b, 0, 4);
            byte[] name = subArray(b, 4, b.length);
            return new StudentKey(bytesToInteger(groupId), bytesToString(name));
        }
    }

    private static byte[] toByteArray(Student obj) {
        byte[] groupId = toByteArray(obj.getGroupId());
        byte[] name = toByteArray(obj.getName());
        byte[] hometown = toByteArray(obj.getHometown());
        byte[] birthDate = toByteArray(obj.getBirthDate());
        byte[] hasDormitory = toByteArray(obj.isHasDormitory());
        byte[] averageScore = toByteArray(obj.getAverageScore());

        return arraysSum(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }

    private static Student bytesToStudent(byte[] b) {
        if (b.length < 8) {
            throw new RuntimeException("Invalid Conversion");
        } else {
            int i = 0;
            int nameLength;
            int hometownLength;

            byte[] groupId = subArray(b, i, i + 4);
            i += 4;

            nameLength = 4 + bytesToInteger(subArray(b, i, i + 4));
            byte[] name = subArray(b, i, i + nameLength);
            i += nameLength;

            hometownLength = 4 + bytesToInteger(subArray(b, i, i + 4));
            byte[] hometown = subArray(b, i, i + hometownLength);
            i += hometownLength;

            byte[] birthDate = subArray(b, i, i + 8);
            i += 8;

            byte[] hasDormitory = subArray(b, i, i + 1);
            i += 1;

            byte[] averageScore = subArray(b, i, i + 8);
            return new Student(bytesToInteger(groupId), bytesToString(name), bytesToString(hometown),
                    bytesToDate(birthDate), bytesToBoolean(hasDormitory), bytesToDouble(averageScore));
        }
    }


    private static byte[] arraysSum(byte[]... arrays) {
        int len = 0;
        for (byte[] array : arrays) {
            len += array.length;
        }
        byte[] completeArray = new byte[len];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, completeArray, offset, array.length);
            offset += array.length;
        }
        return completeArray;
    }

    private static byte[] subArray(byte[] bytes, int l, int r) {
        if ((0 <= l) && (0 <= r) && (l <= r) && (r <= bytes.length)) {
            byte[] ret = new byte[r - l];
            System.arraycopy(bytes, l, ret, 0, r - l);
            return ret;
        } else {
            throw new RuntimeException("Illegal subArray");
        }
    }

}
