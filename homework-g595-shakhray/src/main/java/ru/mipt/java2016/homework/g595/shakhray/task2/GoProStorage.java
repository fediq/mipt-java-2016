package ru.mipt.java2016.homework.g595.shakhray.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import ru.mipt.java2016.homework.tests.task2.*;

/**
 * Created by Vlad on 26/10/2016.
 */
public class GoProStorage implements KeyValueStorage{

    private final String md5prefix = "_md5_prefix";
    private final String databaseName = "st.txt";

    public enum StorageType {
        StudentsStorage,
        IntToDoubleStorage,
        StringStorage
    }
    private StorageType storageType;


    static String absoluteFilePath;
    private Map<String, String> stringsStorage;
    private Map<Integer, Double> intToDoubleStorage;
    private Map<StudentKey, Student> studentStorage;

    private boolean changesMade;

    public GoProStorage(String path, StorageType storageTypePassed) {
        changesMade = false;
        storageType = storageTypePassed;
        stringsStorage = new HashMap<>();
        intToDoubleStorage = new HashMap<>();
        studentStorage = new HashMap<>();
//        if (Files.notExists(path)) {
//            System.out.println("Path not found.");
//            return;
//        }
        absoluteFilePath = path + "" + databaseName;
//        if (Files.exists(absoluteFilePath)) {
//
//        }
        File f = new File(absoluteFilePath);
        if (f.exists() && !f.isDirectory()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(absoluteFilePath), StandardCharsets.UTF_8);
                try {
                    parseStrings(lines);
                } catch (IOException e) {
                    // File corrupted
                }
            } catch (IOException e) {
                // File corrupted
                System.out.println("Corrupted file.");
            }
        }
    }


    private void parseStrings(List<String> lines) throws IOException {
        if (storageType == StorageType.StringStorage) {
            for (int i = 0; i < lines.size()/2; i++) {
                stringsStorage.put(lines.get(i*2), lines.get(2*i+1));
            }
        } else if (storageType == StorageType.IntToDoubleStorage) {
            for (int i = 0; i < lines.size()/2; i++) {
                intToDoubleStorage.put(Integer.parseInt(lines.get(i*2)), Double.parseDouble(lines.get(2*i+1)));
            }
        } else {
            for (int i = 0; i < lines.size()/6; i++) {
                int groupId = Integer.parseInt(lines.get(6*i));
                String name = lines.get(6*i+1);
                String hometown = lines.get(6*i+2);
                boolean hasDormitory = Boolean.parseBoolean(lines.get(6*i+3));
                double averageScore = Double.parseDouble(lines.get(6*i+4));
                Date birthdate = new Date(Long.parseLong(lines.get(6*i+5)));
                StudentKey key = new StudentKey(groupId, name);
                Student value = new Student(groupId, name, hometown, birthdate, hasDormitory, averageScore);
                studentStorage.put(key, value);
            }
        }
    }

    private String getStringByStudent(Student student) {
        String s = "";
        s += student.getHometown() + "\n";
        s += Boolean.toString(student.isHasDormitory()) + "\n";
        s += Double.toString(student.getAverageScore()) + "\n";
        long time = student.getBirthDate().getTime();
        s += Long.toString(time) + "\n";
        return s;
    }

    private String getStringByStudentKey(StudentKey studentKey) {
        String s = Integer.toString(studentKey.getGroupId()) + "\n";
        s += studentKey.getName() + "\n";
        return s;
    }
    private void writeToFile(String text) {
        try (PrintWriter out = new PrintWriter(absoluteFilePath)) {
            out.println(text);
            out.close();
        } catch (FileNotFoundException e) {

        }
    }

    private boolean storageExists() {
        File f = new File(absoluteFilePath);
        if(f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    private String getStorageTypeStringFromInt(Integer key, Double value) {
        String result = Integer.toString(key) + "\n" + Double.toString(value) + "\n";
        return result;
    }

    private String getStorageTypeStringFromString(String key, String value) {
        String result = key + "\n" + value + "\n";
        return result;
    }

    private String md5FromString(String code) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(code.getBytes());
            byte byteData[] = md.digest();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (java.security.NoSuchAlgorithmException e) {

        }

        // Should never be executed
        return "sosi_pisos";
    }

    private String getMD5StorageHash() {
        String md5 = md5FromString(md5prefix);
        if (storageType == StorageType.StringStorage) {
            for (String key: stringsStorage.keySet()) {
                md5 = md5FromString(md5 + key);
                md5 = md5FromString(md5 + stringsStorage.get(key));
            }
            return md5;
        } else if (storageType == StorageType.IntToDoubleStorage) {
            for (Integer key: intToDoubleStorage.keySet()) {
                md5 = md5FromString(md5 + Integer.toString(key));
                md5 = md5FromString(md5 + Double.toString(intToDoubleStorage.get(key)));
            }
            return md5;
        } else if (storageType == StorageType.StudentsStorage) {
            for (StudentKey key: studentStorage.keySet()) {
                md5 = md5FromString(md5 + getStringByStudentKey(key));
                md5 = md5FromString(md5 + getStringByStudent(studentStorage.get(key)));
            }
        }
        return md5;
    }

    @Override
    public Object read(Object key) {
        if (key instanceof Integer) {
            storageType = StorageType.IntToDoubleStorage;
            return intToDoubleStorage.get(key);
        } else if (key instanceof String) {
            storageType = StorageType.StringStorage;
            return stringsStorage.get(key);
        } else if (key instanceof StudentKey) {
            storageType = StorageType.StudentsStorage;
            return studentStorage.get(key);
        }
        return 0;
    }

    @Override
    public boolean exists(Object key) {
        if (key instanceof Integer) {
            return intToDoubleStorage.get(key) != null;
        } else if (key instanceof String) {
            return stringsStorage.get(key) != null;
        } else if (key instanceof StudentKey) {
            return studentStorage.get(key) != null;
        }
        return false;
    }

    @Override
    public void write(Object key, Object value) {
        changesMade = true;
        System.out.println("kljnfjkernfijlnwegjeijwrng");
        if (key instanceof Integer && value instanceof Double) {
            storageType = StorageType.IntToDoubleStorage;
            intToDoubleStorage.put((Integer)key, (Double) value);
        } else if (key instanceof String && value instanceof String) {
            storageType = StorageType.StringStorage;
            stringsStorage.put((String)key, (String)value);
        } else {
            System.out.println("kljnfjkernfijlnwegjeijwrng");
            storageType = StorageType.StudentsStorage;
            studentStorage.put((StudentKey) key, (Student) value);
        }
    }

    @Override
    public void delete(Object key) {
        changesMade = true;
        if (key instanceof Integer) {
            storageType = StorageType.IntToDoubleStorage;
            intToDoubleStorage.remove(key);
        } else if (key instanceof String) {
            storageType = StorageType.StringStorage;
            stringsStorage.remove(key);
        } else if (key instanceof StudentKey) {
            storageType = StorageType.StudentsStorage;
            studentStorage.remove(key);
        }
    }

    @Override
    public Iterator readKeys() {
        return null;
    }

    @Override
    public int size() {
        if (storageType == StorageType.IntToDoubleStorage) {
            return intToDoubleStorage.keySet().size();
        } else if (storageType == StorageType.StringStorage) {
            return stringsStorage.keySet().size();
        } else if (storageType == StorageType.StudentsStorage) {
            return studentStorage.keySet().size();
        }
        return 0;
    }

    @Override
    public void close() throws IOException {
        if (!changesMade) {
            return;
        }
//        String s = getMD5StorageHash() + "\n";
        System.out.println(studentStorage.size());
        String s = "";
        if (storageType == StorageType.StringStorage) {
            for (String key: stringsStorage.keySet()) {
                s += key + "\n";
                s += stringsStorage.get(key) + "\n";
            }
        } else if (storageType == StorageType.IntToDoubleStorage) {
            for (Integer key: intToDoubleStorage.keySet()) {
                s += Integer.toString(key) + "\n";
                s += Double.toString(intToDoubleStorage.get(key)) + "\n";
            }
        } else if (storageType == StorageType.StudentsStorage) {
            for (StudentKey key: studentStorage.keySet()) {
                s += getStringByStudentKey(key);
                s += getStringByStudent(studentStorage.get(key));
            }
        }
        writeToFile(s);
    }
}
