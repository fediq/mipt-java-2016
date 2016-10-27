package ru.mipt.java2016.g595.seminar2;

import org.json.simple.JSONObject;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Student student = new Student(52, "Andrew",
                "Notown", new Date(934786L), false, 0.89);
        StudentKey studentKey = new StudentKey(52, "Andrew");
        JSONObject serialised = new JSONObject();
        JSONObject studentJsonObject = new JSONObject();
        studentJsonObject.put("hometown", student.getHometown());
        studentJsonObject.put("bitrthdate", student.getBirthDate().toString());
        studentJsonObject.put("score", student.getAverageScore());
        studentJsonObject.put("have_dormitory", student.isHasDormitory());
        serialised.put((new Integer(studentKey.getGroupId())).toString() + ";" + studentKey.getName(),
                studentJsonObject);

        System.out.println(serialised.toJSONString());
    }
}
