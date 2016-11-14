package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

/**
 * Created by ${Semien} on ${30.10.16}.
 */
public class Serializations {
    static MySerialization takeSerializer(String str) throws Exception {
        switch (str) {
            case "Integer":
                return new MyIntSerialization();
            case "Double":
                return new MyDoubleSerialization();
            case "String":
                return new MyStringSerialization();
            default:
                throw new Exception("invalid type");
        }
    }
}
