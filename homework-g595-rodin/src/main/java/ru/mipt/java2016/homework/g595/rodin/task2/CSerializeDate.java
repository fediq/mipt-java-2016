package ru.mipt.java2016.homework.g595.rodin.task2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dmitry on 26.10.16.
 */
public class CSerializeDate implements ISerialize<Date> {

    @Override
    public String serialize(Date argument) throws IllegalArgumentException {
        if(argument == null){
            throw new IllegalArgumentException("Null Argument");
        }
        return String.valueOf(argument);
    }

    @Override
    public Date deserialize(String argument) throws IllegalArgumentException {
        if(argument == null){
            throw new IllegalArgumentException("Null Argument");
        }
        DateFormat format = new SimpleDateFormat("MMMM d, YYYY", Locale.ENGLISH);
        Date result;
        try{
            result = format.parse(argument);
        } catch (ParseException exception){
            throw new IllegalArgumentException("Invalid Argument");
        }
        return result;
    }

}
