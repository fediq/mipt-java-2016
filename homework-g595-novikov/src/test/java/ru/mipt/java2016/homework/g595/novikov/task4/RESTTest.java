package ru.mipt.java2016.homework.g595.novikov.task4;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Created by igor on 11/30/16.
 */

// Copy-pasted from : https://spring.io/guides/gs/spring-boot/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RESTTest extends AbstractCalculatorStateTest {
    @Autowired private MockMvc mvc;

    @Override
    protected CalculatorWithMethods calc() {
        return new CalculatorOverREST(mvc);
    }
}
