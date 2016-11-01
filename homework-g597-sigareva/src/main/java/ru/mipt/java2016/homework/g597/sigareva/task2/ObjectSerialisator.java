package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;

import java.io.*;

abstract class ObjectSerialisator<K, V> {

    abstract void write(K key, V value);

    abstract Pair<K, V> read() throws IOException;

    protected BufferedReader inputStream;
    protected PrintWriter outputStream;
    private String path;
    private String myFile;
    private boolean goodFile = true;

    public boolean isGoodFile() {
        return goodFile;
    }

    protected ObjectSerialisator(String newPath) {
        path = newPath;
        StringBuilder adress = new StringBuilder();
        adress.append(path);
        adress.append("\\Lenin_luchshiy"); // название хранилища
        myFile =  adress.toString();

        if ((new File(myFile)).exists()) {
            try {
                inputStream = new BufferedReader(new FileReader(myFile));
                String firstString = inputStream.readLine();
                if (!firstString.equals("Lenin is the best. Lenin is my love!")) {
                    goodFile = false;
                }
                inputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Something is bad\n");
            }

        } else {
            File f = new File(myFile);
            try {
                f.createNewFile();
                outputStream = new PrintWriter(new FileWriter(myFile));
                outputStream.print("Lenin is the best. Lenin is my love!");
                openOutputStream = true;
                outputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Something is bad\n");
            }
        }
    }

    private boolean openInputStream = false;
    private boolean openOutputStream = false;

    public void checkBeforeRead() {
        if (openInputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Something is bad\n");
            }
        }
        if (openOutputStream) {
            outputStream.close();
        }
        try {
            inputStream = new BufferedReader(new FileReader(myFile));
            inputStream.readLine();
            openInputStream = true;
        } catch (IOException e) {
            throw new IllegalStateException("Something is bad\n");
        }
    }

    public void checkBeforeWrite() {
        if (openInputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Something is bad\n");
            }
        }
        if (openOutputStream) {
            outputStream.close();
        }
        try {
            outputStream = new PrintWriter(new FileWriter(myFile));
            openOutputStream = true;
            outputStream.print("Lenin is the best. Lenin is my love!\n");
        } catch (IOException e) {
            throw new IllegalStateException("Something is bad\n");
        }
    }
}
