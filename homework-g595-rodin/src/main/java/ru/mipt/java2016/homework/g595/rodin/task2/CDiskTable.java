package ru.mipt.java2016.homework.g595.rodin.task2;

import java.io.*;

import static java.nio.file.Files.exists;

/**
 * Created by Dmitry on 26.10.16.
 */
public class CDiskTable {

    private final String directoryPath;

    private final File file;

   public CDiskTable(String directoryPath)throws RuntimeException{
       this.directoryPath = directoryPath;
       file = new File(directoryPath);
   }

   public void createFile(){
       try {
           file.createNewFile();
       } catch (IOException exeption) {
           throw  new RuntimeException(exeption.getMessage());
       }
   }

    public void write(String text) throws RuntimeException{
        try {
            PrintWriter outWriter = new PrintWriter(file.getAbsoluteFile());
            outWriter.print(text);
        } catch (FileNotFoundException exception)
        {
            throw new RuntimeException("File not found");
        }
    }

    public  String read() throws FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        exists();
        try {
            BufferedReader reader
                    = new BufferedReader(new FileReader( file.getAbsoluteFile()));
            try {
                String s;
                while ((s = reader.readLine()) != null) {
                    stringBuilder.append(s);
                    stringBuilder.append("\n");
                }
            } finally {
                reader.close();
            }
        } catch(IOException exception) {
            throw new RuntimeException(exception);
        }
        return stringBuilder.toString();
    }

    public  boolean exists() throws FileNotFoundException {
        if (!file.exists()){
            throw new FileNotFoundException(file.getName());
        }
        return true;
    }
}
