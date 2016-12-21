package ru.mipt.java2016.homework.g597.vasilyev.task1;

/**
 * Created by mizabrik on 21.12.16.
 */
public interface Scope {
    Command getCommand(String name);

    boolean hasCommand(String name);
}
