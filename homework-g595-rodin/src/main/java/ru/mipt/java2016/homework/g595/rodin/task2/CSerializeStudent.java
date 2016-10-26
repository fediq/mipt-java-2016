package ru.mipt.java2016.homework.g595.rodin.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Dmitry on 26.10.16.
 */
public class CSerializeStudent implements ISerialize<Student> {
    @Override
    public String serialize(Student argument) throws IllegalArgumentException {
        if(argument == null){
            throw new IllegalArgumentException("Null Argument");
        }
        CSerializeString serializeString = new CSerializeString();
        CSerializeInteger serializeInteger = new CSerializeInteger();
        CSerializeDate serializeDate = new CSerializeDate();
        CSerializeBoolean serializeBoolean = new CSerializeBoolean();
        CSerializeDouble serializeDouble = new CSerializeDouble();
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("\"groupId\" : ")
                .append(serializeInteger.serialize(argument.getGroupId())).append(",")
                .append("\"name\" :").append(serializeString.serialize(argument.getName()))
                .append(",").append("\"hometown\" :")
                .append(serializeString.serialize(argument.getHometown())).append(",")
                .append("\"birthDate\" : ")
                .append(serializeDate.serialize(argument.getBirthDate())).append(",")
                .append("\"hasDormitory\" : ")
                .append(serializeBoolean.serialize(argument.isHasDormitory())).append(",")
                .append("\"averageScore\" : ")
                .append(serializeDouble.serialize(argument.getAverageScore())).toString();

    }

    @Override
    public Student deserialize(String argument) throws IllegalArgumentException {
        if(argument == null){
            throw new IllegalArgumentException("Null Argument");
        }
        CSerializeString serializeString = new CSerializeString();
        CSerializeInteger serializeInteger = new CSerializeInteger();
        CSerializeDate serializeDate = new CSerializeDate();
        CSerializeBoolean serializeBoolean = new CSerializeBoolean();
        CSerializeDouble serializeDouble = new CSerializeDouble();
        StringTokenizer tokenizer = new StringTokenizer(argument,"\",:",false);

        Integer groupId;
        String name;
        String hometown;
        Date birthdate;
        Boolean hasDormitory;
        Double averageScore;

        String token = tokenizer.nextToken();
        if(token != "groupId"){
            throw new IllegalArgumentException("Invalid Argument");
        }
        token = tokenizer.nextToken();
        groupId = serializeInteger.deserialize(token);

        token = tokenizer.nextToken();
        if(token != "name"){
            throw new IllegalArgumentException("Invalid Argument");
        }
        token = tokenizer.nextToken();
        name = serializeString.deserialize(token);

        token = tokenizer.nextToken();
        if(token != "hometown"){
            throw new IllegalArgumentException("Invalid Argument");
        }
        token = tokenizer.nextToken();
        hometown = serializeString.deserialize(token);

        token = tokenizer.nextToken();
        if(token != "birthDate"){
            throw new IllegalArgumentException("Invalid Argument");
        }
        token = tokenizer.nextToken();
        birthdate = serializeDate.deserialize(token);

        token = tokenizer.nextToken();
        if(token != "hasDormitory"){
            throw new IllegalArgumentException("Invalid Argument");
        }
        token = tokenizer.nextToken();
        hasDormitory = serializeBoolean.deserialize(token);

        token = tokenizer.nextToken();
        if(token != "averageScore"){
            throw new IllegalArgumentException("Invalid Argument");
        }
        token = tokenizer.nextToken();
        averageScore = serializeDouble.deserialize(token);
        if(tokenizer.hasMoreTokens())
        {
            throw new IllegalArgumentException("Invalid Argument");
        }
        return new Student(groupId,name,hometown,birthdate,hasDormitory,averageScore);
    }
}
