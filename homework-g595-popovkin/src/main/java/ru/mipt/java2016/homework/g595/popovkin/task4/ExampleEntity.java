package ru.mipt.java2016.homework.g595.popovkin.task4;

import java.util.List;

/**
 * Created by malchun on 11/26/16.
 */
public class ExampleEntity {
    private String name;
    private List<String> args;
    private String content;
    public ExampleEntity(String name, List<String> args, String content) {
        this.content = content;
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getContent() {
        return content;
    }
    @Override
    public String toString() {
        return name + " : " + args + " contains " + content;
    }
}
