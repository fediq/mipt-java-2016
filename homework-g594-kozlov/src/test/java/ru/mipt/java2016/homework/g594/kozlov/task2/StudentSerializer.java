package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerUtil;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class StudentSerializer implements SerializerInterface<Student> {
    @Override
    public String serialize(Student objToSerialize) {
        if (objToSerialize == null) {
            return null;
        }
        StringBuilder resultString = new StringBuilder("{");
        StudentKeySerializer studKeySer;
        resultString.append(SerializerUtil.writeMemberInt("groupId", objToSerialize.getGroupId()))
                .append(',')
                .append(SerializerUtil.writeMemberString("name", objToSerialize.getName()))
                .append(',')
                .append(SerializerUtil.writeMemberString("hometown", objToSerialize.getHometown()))
                .append(',')
                .append(SerializerUtil.writeMemberBoolean("dorm", objToSerialize.isHasDormitory()))
                .append(',')
                .append(SerializerUtil.writeMemberDate("date", objToSerialize.getBirthDate()))
                .append(',')
                .append(SerializerUtil.writeMemberDouble("score", objToSerialize.getAverageScore()))
                .append('}');
        return resultString.toString();
    }

    @Override
    public Student deserialize(String inputString) throws StorageException {
        SerializerUtil.checkBracket(inputString.charAt(0));
        SerializerUtil.checkBracket(inputString.charAt(inputString.length() - 1));
        String[] tokens = inputString.substring(1, inputString.length() - 1).split(",");
        if (tokens.length < 6) {
            throw new StorageException("Reading error");
        }
        int objectGroupId = SerializerUtil.readMemberInt("groupId", tokens[0]);
        String objectName = SerializerUtil.readMemberString("name", tokens[1]);
        String objectHomeTown = SerializerUtil.readMemberString("hometown", tokens[2]);
        Boolean objectFlag = SerializerUtil.readMemberBoolean("dorm", tokens[3]);
        Date objectDate = SerializerUtil.readMemberDate("date", tokens[4]);
        Double objectScore = SerializerUtil.readMemberDouble("score", tokens[5]);
        return new Student(objectGroupId, objectName, objectHomeTown,
                objectDate, objectFlag, objectScore);
    }

    @Override
    public String getClassString() {
        return "Student";
    }
}
