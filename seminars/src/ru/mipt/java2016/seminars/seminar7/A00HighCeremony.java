package ru.mipt.java2016.seminars.seminar7;

public class A00HighCeremony {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.print("In thread");
            }
        });

        Thread t2 = new Thread(() -> System.out.print("In thread"));
    }
}
