package ru.mipt.java2016.seminars.seminar1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Пример работы с сериализацией
 */
public class SerializationDemo {
    private static class UserProfile implements Serializable {
        private String name;
        private transient int id; // не сериализуется
        private double rating;

        UserProfile(String name, double rating) {
            this.name = name;
            this.rating = rating;
        }

        void setId(int id) {
            this.id = id;
        }
    }

    public static void main(String[] args) {
        UserProfile profile = new UserProfile("Vasya", 105.0);
        profile.setId(12345);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             new FileOutputStream("ObjectSerialization.txt"))) {
            outputStream.writeObject(profile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(
                             new FileInputStream("ObjectSerialization.txt"))) {
            UserProfile deserializedProfile = (UserProfile) inputStream.readObject();
            System.out.println("Deserialized profile:");
            System.out.println("Name: " + deserializedProfile.name);
            System.out.println("Id: " + deserializedProfile.id);
            System.out.println("Rating: " + deserializedProfile.rating);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e);
        }
    }
}
