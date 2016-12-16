package ru.mipt.java2016.homework.g595.murzin.task4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by dima on 16.12.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyApplication.class)
public class MySecurityTest {
    @Autowired
    private MyController controller;
    @Autowired
    private BillingDao billingDao;

    @FunctionalInterface
    public interface Callback {
        void callback() throws Exception;
    }

    private void doWith(MyControllerTest.Callback callback) throws Exception {
        doWith("user", "", callback);
    }

    private void doWith(String username, MyControllerTest.Callback callback) throws Exception {
        doWith(username, "", callback);
    }

    private void doWith(String username, String password, MyControllerTest.Callback callback) throws Exception {
        billingDao.deleteAll();
        controller.register(username, password);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, password));
        callback.callback();
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testMissingUser() throws Exception {
        doWith(() -> {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("abcde", "12345"));
            evalSomething();
        });
    }

    private void evalSomething() {
        controller.eval("2+2");
    }

    @Test
    public void testRegister() throws Exception {
        doWith("abcde", "12345", this::evalSomething);
    }

    @Test
    public void testAllUsers() throws Exception {
        doWith(() -> {
            controller.register("a", "");
            controller.register("b", "");
            assertArrayEquals(billingDao.getAllUserNames(), new String[]{"a", "b"});
        });
    }
}
