package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class StringSerializer implements SerializerInterface<String> {
    @Override
    public String serialize(String objToSerialize) {
        if (objToSerialize == null) {
            return null;
        }
        /* try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            GZIPOutputStream gzip = new GZIPOutputStream(bout);
            gzip.write(objToSerialize.getBytes());
            gzip.close();
            return bout.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        return objToSerialize;
    }

    @Override
    public String deserialize(String inputString) {
        if (inputString == null) {
            return null;
        }
        /*try (ByteArrayInputStream bin = new ByteArrayInputStream(inputString)) {
            GZIPInputStream gzip = new GZIPInputStream(bin);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len;
            while ((len = gzip.read(buff)) != -1) {
                bout.write(buff, 0, len);
            }
            gzip.close();
            bout.close();
            return new String(bout.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        return inputString;
    }

    @Override
    public String getClassString() {
        return "String";
    }
}
