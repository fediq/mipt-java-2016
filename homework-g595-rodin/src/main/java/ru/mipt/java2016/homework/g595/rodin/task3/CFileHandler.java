package ru.mipt.java2016.homework.g595.rodin.task3;

import java.io.*;
import java.nio.ByteBuffer;


public class CFileHandler implements Closeable {

    private final File file;

    private final String fileName;

    private BufferedInputStream inputStream = null;

    private BufferedOutputStream outputStream = null;

    private long offset = 0;

    CFileHandler(String fileName){
        file = new File(fileName);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean exists() {
        return file.exists();
    }

    public void createFile() {
        try {
            file.createNewFile();
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    private void checkExistence() throws FileNotFoundException{
        if(!exists()) {
            throw new RuntimeException("File not found " + fileName);
        }
    }

    private void checkOutputBuffer(){
        try {
            if (outputStream == null) {
                outputStream = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile(),true));
            }
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    private void checkInputBuffer(){
        try {
            if(inputStream == null){
                inputStream = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
                offset = 0;
            }
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    private void flushOutput(){
        try{
            outputStream.flush();
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    private void closeInput(){
        try{
            inputStream.close();
            inputStream = null;
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    public long write(String text){
        try {
            checkExistence();
            checkOutputBuffer();
            outputStream.write(ByteBuffer.allocate(4).putInt(text.length()).array());
            outputStream.write(text.getBytes());
            return text.getBytes().length + 4;
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }
    }

    public long append(String text){
        try {
            checkExistence();
            checkOutputBuffer();
            byte[] textLength = ByteBuffer.allocate(4).putInt(text.getBytes().length).array();
            outputStream.write(textLength);
            outputStream.write(text.getBytes());
            long temp = offset;
            offset += text.getBytes().length + 4;
            return temp;
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    public String readToken(){
        try {
            checkExistence();
            checkInputBuffer();
            if(inputStream.available() < 4){
                closeInput();
                return null;
            }
            byte[] buffer =  new byte[4];
            int readLength = inputStream.read(buffer, 0, 4);
            if(readLength < 4){
                throw new RuntimeException("Cannot read data");
            }
            int length = ByteBuffer.wrap(buffer).getInt();
            offset += readLength;
            buffer = new byte[length];
            readLength = inputStream.read(buffer, 0, length);
            if(readLength < length){
                throw new RuntimeException("Cannot read data");
            }
            offset += readLength;
            return new String(buffer);
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }


    public void reposition(long targetOffset){
        try{
            checkExistence();
            checkInputBuffer();
            if(offset > targetOffset){
                closeInput();
                checkInputBuffer();
            }
            long shift = inputStream.skip(targetOffset - offset);
            while(shift != 0) {
                offset += shift;
                inputStream.skip(targetOffset - offset);
            }
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }


    public String loadKey(long offset) {
        reposition(offset);
        return readToken();
    }


    public void delete(){
        file.delete();
    }

    @Override
    public void close() {
            closeInput();
            flushOutput();
    }


}
