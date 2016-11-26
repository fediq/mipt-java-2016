package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Morell on 29.10.2016.
 */
public class KVStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String,String> buildStringsStorage(String path) {
        DataBase<String,String> dataBase = null;
        try{
            dataBase = new DataBase<String,String>( path, new SString(), new SString() );

        }
        catch( IOException ex ){
            System.out.println(ex.getMessage());
        }
        return dataBase;
    }

    @Override
    protected KeyValueStorage<Integer,Double> buildNumbersStorage(String path) {
        DataBase<Integer,Double> dataBase = null;
        try{
            dataBase = new DataBase<Integer,Double>( path, new SInteger(), new SDouble() );
            return dataBase;
        }
        catch( IOException ex ){
            System.out.println(ex.getMessage());
        }
        return dataBase;
    }

    @Override
    protected KeyValueStorage<StudentKey,Student> buildPojoStorage(String path) {
        DataBase<StudentKey,Student> dataBase = null;
        try{
            dataBase = new DataBase<StudentKey,Student>( path, new SStudentKey(), new SStudentVal() );
        }
        catch( IOException ex ){
            System.out.println(ex.getMessage());
        }
        return dataBase;
    }
}
