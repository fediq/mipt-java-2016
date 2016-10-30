package ru.mipt.java2016.homework.g594.stepanov.task2;

import javafx.util.Pair;

import java.io.*;

public abstract class ObjectSerializator<K, V> {
    abstract void write(K key, V value) throws IOException;
    abstract Pair<K, V> read() throws IOException;
    String start = "[";
    String numerator = ",";
    String separator = ":";
    String finish = "]";
    public BufferedReader inputStream;
    public PrintWriter outputStream;

    public ObjectSerializator(String directory) throws IOException {
        path = directory;
        System.out.println("GO");
        File dir = new File(directory);
        System.out.println(directory);
        if (!dir.exists()) {
            System.out.println("GO!!!!!!!!!");
            dir.mkdir();
            dir.createNewFile();
        } else {
            System.out.println("EXISTS");
        }
        System.out.println(directory);
        StringBuilder builder = new StringBuilder();
        builder.append(path);
        builder.append("/Storage.db");
        storageName = builder.toString();
        if ((new File(storageName).exists())) {
            if (!validate()) {
                validState = false;
            }
        } else {
            System.out.println("Create\n");
            File tmp = new File(storageName);
            tmp.createNewFile();
        }
        System.out.println(storageName);
    }


    boolean validate() throws IOException {
        System.out.println("LOL");
        StringBuilder builder = new StringBuilder();
        builder.append(path);
        builder.append("/Storage.db");
        storageName = builder.toString();
        inputStream = new BufferedReader(new FileReader(storageName));
        while (true) {
            try {
                Pair<K, V> p = read();
                currentHash += p.hashCode();
            } catch (IOException e) {
                if (e.getMessage().equals("File end")) {
                    break;
                } else {
                    validState = false;
                    inputStream.close();
                    return false;
                }
            }
        }
        builder = new StringBuilder();
        builder.append(path);
        builder.append("/HashCode.txt");
        hashName = builder.toString();
        BufferedReader hashReader = new BufferedReader(new FileReader(hashName));
        String writtenHash = hashReader.readLine();
        if (Integer.parseInt(writtenHash) != currentHash) {
            validState = false;
        }
        hashReader.close();
        inputStream.close();
        return validState;
    }

    void terminate() throws IOException {
        if (writing) {
            outputStream.close();
            writing = false;
        }
        if (reading) {
            try {
                inputStream.close();
            } catch (Exception e) {
                //do nothing
            }

            reading = false;
        }
    }

    void getReadyToRead() {
        try {
            terminate();
            System.out.println("DONE1\n");
            inputStream = new BufferedReader(new FileReader(storageName));
            System.out.println("DONE2\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        reading = true;
    }

    void getReadyToWrite() {
        try {
            terminate();
            outputStream = new PrintWriter(new File(storageName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        writing = true;
    }

    void writeHash() throws IOException {
        terminate();
        outputStream = new PrintWriter(new File(hashName));
        outputStream.print(currentHash);
        outputStream.close();
    }

    Integer currentHash = 0;
    boolean validState = true;
    String path;
    String storageName;
    String hashName;

    boolean writing = false;
    boolean reading = false;

}

