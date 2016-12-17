package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

import java.io.IOException;
import java.io.RandomAccessFile;
//import ru.mipt.java2016.homework.g595.efimochkin.task3.BaseSerialization;

/**
 * Created by sergejefimockin on 28.11.16.
 */
public class BooleanSerialization implements BaseSerialization<Boolean> {

    private static BooleanSerialization instance = new BooleanSerialization();

    public static BooleanSerialization getInstance() {return instance;}

    private BooleanSerialization() { }


    @Override
    public Boolean read(RandomAccessFile fileName) throws IOException {
        return fileName.readBoolean();
    }

    @Override
    public Long write(RandomAccessFile fileName, Boolean data) throws IOException {
        Long offset = fileName.getFilePointer();
        fileName.writeBoolean(data);
        return offset;    }
}
