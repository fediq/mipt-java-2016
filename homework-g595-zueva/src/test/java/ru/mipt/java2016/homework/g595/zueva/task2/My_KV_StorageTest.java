package ru.mipt.java2016.homework.g595.zueva.task2;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class My_KV_StorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        MyKVStorage result = null;
        Serializers.SerializerString a;
        Serializers.SerializerString b;
        try {
            result = new MyKVStorage(path, new Serializers.SerializerString(),
                    new Serializers.SerializerString());
        } catch (Exception except) {
            System.out.println(except.getMessage());
        }
        return result;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MyKVStorage<Integer, Double> result = null;
        try {
            result = new MyKVStorage(path, new Serializers.SerialiserInt(),
                    new Serializers.SerializerDouble());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public KeyValueStorage<ru.mipt.java2016.homework.tests.task2.StudentKey, ru.mipt.java2016.homework.tests.task2.Student> buildPojoStorage(String path) {
        MyKVStorage result = null;
        try {
            result = new MyKVStorage(path, new SerializerStudentKey(),
                    new SerializerStudent());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
class SerializerStudentKey implements Serializer<StudentKey> {

    public void writeToStream(DataOutputStream out, StudentKey value) throws IOException {
        out.writeInt(value.getGroupId());
        out.writeUTF(value.getName());
    }

    public StudentKey readFromStream(DataInputStream in) throws IOException {
        return new StudentKey(in.readInt(), in.readUTF());
    }
}

class SerializerStudent implements Serializer<Student> {

    public void writeToStream(DataOutputStream out, Student value) throws IOException {
        out.writeInt(value.getGroupId());
        out.writeUTF(value.getName());
        out.writeUTF(value.getHometown());
        out.writeLong(value.getBirthDate().getTime());
        out.writeBoolean(value.isHasDormitory());
        out.writeDouble(value.getAverageScore());

    }

    public Student readFromStream(DataInputStream in) throws IOException {
        return new Student(in.readInt(), in.readUTF(), in.readUTF(),
                new Date(in.readLong()), in.readBoolean(),
                in.readDouble());
    }
}

