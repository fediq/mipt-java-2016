package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;

import java.io.*;

abstract class ObjectSerialisator<K, V> {
    abstract void write(K key, V value);
    abstract Pair<K, V> read() throws IOException;

    BufferedReader inputStream;
    PrintWriter outputStream;
    private String path;
    private String myFile;
    boolean goodFile = true;

    ObjectSerialisator(String newPath){
        path = newPath;
        StringBuilder adress = new StringBuilder();
        adress.append(path);
        adress.append("\\Lenin_luchshiy"); // название хранилища
        myFile =  adress.toString();

        if ((new File(myFile)).exists()) {
            try{
                inputStream = new BufferedReader(new FileReader(myFile));
                String firstString = inputStream.readLine();
                if(!firstString.equals("Lenin is the best. Lenin is my love!")){
                    goodFile = false;
                }
                inputStream.close();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }

        } else {
            File f = new File(myFile);
            try {
                f.createNewFile();
                outputStream = new PrintWriter(new FileWriter(myFile));
                outputStream.print("Lenin is the best. Lenin is my love!");
                openOutputStream = true;
                outputStream.close();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    boolean openInputStream = false;
    boolean openOutputStream = false;

    void CheckBeforeRead(){
        if(openInputStream){
            try {
                inputStream.close();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if(openOutputStream){
            outputStream.close();
        }
        try{
            inputStream = new BufferedReader(new FileReader(myFile));
            String someString = inputStream.readLine();
            openInputStream = true;
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    void CheckBeforeWrite(){
        if(openInputStream){
            try {
                inputStream.close();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if(openOutputStream){
            outputStream.close();
        }
        try{
            outputStream = new PrintWriter(new FileWriter(myFile));
            openOutputStream = true;
            outputStream.print("Lenin is the best. Lenin is my love!\n");
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


}
