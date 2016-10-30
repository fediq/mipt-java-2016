package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

import ru.mipt.java2016.homework.g594.kozlov.task2.StorageException;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class StudentSerializer implements SerializerInterface<Student> {
    @Override
    public String serialize(Student objToSerialize) {
        StringBuilder resultString = new StringBuilder("{");
        StudentKeySerializer studKeySer;
        System.out.println(objToSerialize.getBirthDate());
        resultString.append(SerializerUtil.writeMemberInt("groupId", objToSerialize.getGroupId()))
                .append(',')
                .append(SerializerUtil.writeMemberString("name", objToSerialize.getName()))
                .append(',')
                .append(SerializerUtil.writeMemberString("hometown", objToSerialize.getHometown()))
                .append(',')
                .append(SerializerUtil.writeMemberBoolean("dorm", objToSerialize.isHasDormitory()))
                .append(',')
                .append(SerializerUtil.writeMemberDate("date", objToSerialize.getBirthDate())) //TODO
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
        String objectName;
        int objectGroupId;
        String objectHomeTown;
        Boolean objectFlag;
        Double objectScore;
        Date objectDate;
        objectGroupId = SerializerUtil.readMemberInt("groupId", tokens[0]);
        objectName = SerializerUtil.readMemberString("name", tokens[1]);
        objectHomeTown = SerializerUtil.readMemberString("hometown", tokens[2]);
        objectFlag = SerializerUtil.readMemberBoolean("dorm", tokens[3]);
        objectDate = SerializerUtil.readMemberDate("date", tokens[4]);
        objectScore = SerializerUtil.readMemberDouble("score", tokens[5]);
        System.out.println(objectGroupId);
        System.out.println(objectName);
        System.out.println(objectHomeTown);
        System.out.println(objectFlag);
        System.out.println(objectScore);
        System.out.println(objectDate);
        return new Student(objectGroupId, objectName, objectHomeTown,
                objectDate, objectFlag, objectScore);
    }
}
