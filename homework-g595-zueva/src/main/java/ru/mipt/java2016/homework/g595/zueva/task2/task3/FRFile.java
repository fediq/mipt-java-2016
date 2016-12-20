package ru.mipt.java2016.homework.g595.zueva.task2.task3;
import java.io.*;
import java.nio.*;
/**
 * Created by someb on 17.12.2016.
 */
public class FRFile {
    private ByteBuffer mybuffer = ByteBuffer.allocate(Long.SIZE / 8);
    private File myfile;
    private BufferedInputStream in;
    private BufferedOutputStream out;

     private boolean ifEmpty = false;

             public FRFile(String fileDirectory, String fileName) throws IOException {
                File directory = new File(fileDirectory);
                if (directory.exists()) {
                        myfile = new File(fileDirectory, fileName);
                    myfile.createNewFile();
                        in = new BufferedInputStream(new FileInputStream(myfile));
                    } else {
                        throw new IOException("Cannot found directory");
                    }
            }

             public long ReadFileOffst() throws IOException {
                if (in.read(mybuffer.array(), 0, Long.SIZE / 8) != Long.SIZE / 8) {
                        throw new IOException("Can`t continue reading");
                    }
                return mybuffer.getLong(0);
            }
    public int readKey() throws IOException {
        if (in.read(mybuffer.array(), 0, Integer.SIZE / 8) != Integer.SIZE / 8) {
            throw new IOException("Can`t continue reading");
        }
        return mybuffer.getInt(0);
    }

    public void writeBytes(ByteBuffer bytesToWrite) throws IOException {
        out.write(bytesToWrite.array());
    }

             public void writeFileOffst(long value) throws IOException {
                mybuffer.putLong(0, value);
                out.write(mybuffer.array());
            }

             public ByteBuffer byteReading(int size) throws IOException {
                ByteBuffer bytesRead = ByteBuffer.allocate(size);
                in.read(bytesRead.array(), 0, size);
                return bytesRead;
            }


             public void SizeWr(int size) throws IOException {
                mybuffer.putInt(0, size);
                out.write(mybuffer.array(), 0, Integer.SIZE / 8);
            }

             public void Clean() throws IOException {
                ifEmpty = true;
                in.close();
                myfile.delete();
                myfile.createNewFile();
                out = new BufferedOutputStream(new FileOutputStream(myfile));
            }

             public void close() throws IOException {
                if (ifEmpty) {
                        mybuffer.putInt(0, Integer.MIN_VALUE);
                        out.write(mybuffer.array(), 0, Integer.SIZE / 8);
                        out.close();
                    } else {
                        in.close();
                    }
            }

             public boolean checkIsEmpty() throws IOException {
                return in.available() == 0;
            }
}
