package ru.mipt.java2016.seminars.seminar1;

import java.io.IOException;

/**
 * Пример работы с исключениями
 */
public class ExceptionsDemo {
    public static void main(String[] args) {

    }

    // Исключение обрабатывается внутри метода
    private static void handlingExceptionMethod() {
        System.out.println("In handlingExceptionMethod()");
        try {
            throw new IOException("Oops");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("After throw");
    }

    // Метод бросает checked-исключение и декларирует это
    private static void throwingExceptionMethod() throws IOException {
        System.out.println("In throwingExceptionMethod()");
        throw new IOException("Oops");
        //System.out.println("After throw");
    }

    // Метод бросает unchecked-исключение и никому об этом не говорит
    private static void throwingUncheckedExceptionMethod() {
        System.out.println("In throwingUncheckedExceptionMethod()");
        throw new NullPointerException("Oops");
        //System.out.println("After throw");
    }
}
