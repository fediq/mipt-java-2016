package ru.mipt.java2016.homework.g595.zueva.task2;
/*created by nestyme on 31.10.16*/
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;


public class My_KV_StorageTest extends AbstractSingleFileStorageTest {

    @Override
    public KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MyKVStorage<Integer, Double> answer1 = null;
        try {
            answer1 = new MyKVStorage(path, new Specified_serializers.SerialiserInt(),
                    new Specified_serializers.SerializerDouble());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return answer1;
    }

    @Override
    public KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        MyKVStorage answer2 = null;
        try {
            answer2 = new MyKVStorage(path, new Specified_serializers.SerializerStudentKey(),
                    new Specified_serializers.SerializerStudent());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return answer2;
    }

    @Override
    public KeyValueStorage<String, String> buildStringsStorage(String path) {
        Specified_serializers.SerializerString a;
        Specified_serializers.SerializerString b;
        MyKVStorage answer3 = null;
        try {
            answer3 = new MyKVStorage(path, new Specified_serializers.SerializerString(),
                    new Specified_serializers.SerializerString());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return answer3;
    }
}


