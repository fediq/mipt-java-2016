package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerUtil;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class StudentKeySerializer implements SerializerInterface<StudentKey> {
    @Override
    public String serialize(StudentKey objToSerialize) {
        if (objToSerialize == null) {
            return null;
        }
        StringBuilder resultString = new StringBuilder("{");
        resultString.append(SerializerUtil.writeMemberInt("groupId", objToSerialize.getGroupId()))
                .append(",")
                .append(SerializerUtil.writeMemberString("name", objToSerialize.getName()))
                .append("}");
        return resultString.toString();
    }

    @Override
    public StudentKey deserialize(String inputString) throws StorageException {
        SerializerUtil.checkBracket(inputString.charAt(0));
        SerializerUtil.checkBracket(inputString.charAt(inputString.length() - 1));
        String[] tokens = inputString.substring(1, inputString.length() - 1).split(",");
        if (tokens.length < 2) {
            throw new StorageException("Reading error");
        }
        String objectName;
        int objectGroupId;
        objectGroupId = SerializerUtil.readMemberInt("groupId", tokens[0]);
        objectName = SerializerUtil.readMemberString("name", tokens[1]);
        return new StudentKey(objectGroupId, objectName);
    }

    @Override
    public String getClassString() {
        return "StudentKey";
    }

}
