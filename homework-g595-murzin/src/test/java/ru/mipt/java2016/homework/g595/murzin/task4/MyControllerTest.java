package ru.mipt.java2016.homework.g595.murzin.task4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by dima on 01.12.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyController.class)
public class MyControllerTest {

    @Autowired
    private MyController controller;

    @Test
    public void testSingleVariable() throws Exception {
        String variableName = "x";
        String variableValue = "1 + 2 + 3";
        controller.putVariable(variableName, variableValue);
        assertEquals(controller.getVariable(variableName).getBody(), variableValue);
        assertEquals(controller.eval(variableName).getBody(), 6, 1e-6);
        assertArrayEquals(controller.getVariables(), new String[]{variableName});
        controller.deleteVariable(variableName);
        assertEquals(controller.getVariable(variableName).getStatusCode(), HttpStatus.NOT_FOUND);
        assertArrayEquals(controller.getVariables(), new String[0]);
    }
}
