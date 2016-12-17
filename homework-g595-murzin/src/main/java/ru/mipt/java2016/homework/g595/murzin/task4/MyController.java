package ru.mipt.java2016.homework.g595.murzin.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.murzin.task1.MyContext;
import ru.mipt.java2016.homework.g595.murzin.task1.SimpleCalculator;

import java.util.List;
import java.util.Set;

/**
 * Created by dima on 11/26/16.
 */

// DONE втроенные функции (sin, log, min, rnd, ...)
// DONE REST контроллер
// DONE Авторизация с регистрацией
// DONE Пользовательские функции и переменные (состояние сохраняется в базу данных)
// DONE Тесты REST (Сейчас используется аннотация @WithMockUser)
// TODO Немного изменить тесты REST (класс MyControllerTest), чтобы запускались без аннотации @WithMockUser
// Нужно каждый вызов get(...) заменить на get(...).with(user("username").roles("USER"))
// Либо нагуглить как это по нормальному сделать
// Либо оставить с @WithMockUser (кажется это лучший вариант)
// TODO Тесты регистрации/авторизации +проверки, что доступ к /clearAll имеют только пользователи с roles("ADMIN")
// Тут точно понадобится get(...).with(user("username").roles("USER"))
// TODO сейчас почти все методы MyController возвращают ResponseEntity<...>, это нужно для того, чтобы при возникновении Exception поставить какой-нибудь 4xx код возврата
// Нужно убрать ResponseEntity (То есть ResponseEntity<String> -> String), при возникновении Exception кидать свою, проаннотировать её как в https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
// TODO После выполнения предыдущего TODO проверить, что все неккоректные запросы выдают код 4xx и (!!!) что в их body написана причина ошибки
// TODO Web интерфейс
@RestController
public class MyController {
    private static boolean checkName(String identifier) {
        return identifier.matches("[_a-zA-Z][_0-9a-zA-Z]*");
    }

    @Autowired
    private BillingDao billingDao;

    private ContextProvider getContextProvider() {
        return new ContextProvider(billingDao);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Void> register(@RequestParam(value = "username") String username,
                                         @RequestParam(value = "password") String password) {
        try {
            billingDao.registerUser(username, password);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // variables
    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.GET)
    public ResponseEntity<String> getVariable(@PathVariable String variableName) {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            return context.variables.containsKey(variableName) ?
                    new ResponseEntity<>(context.variables.get(variableName).expression, HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.PUT)
    public ResponseEntity<Void> putVariable(@PathVariable String variableName, @RequestBody String variableExpression)
            throws ParsingException {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            boolean success = checkName(variableName) && context.setVariable(variableName, variableExpression);
            return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteVariable(@PathVariable String variableName) {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            boolean success = context.variables.containsKey(variableName);
            context.variables.remove(variableName);
            return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/variable/", method = RequestMethod.GET)
    public String[] getVariables() {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            Set<String> variables = context.variables.keySet();
            return variables.toArray(new String[variables.size()]);
        }
    }

    // functions
    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.GET)
    public ResponseEntity<MyFunction> getFunction(@PathVariable String functionName) {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            return context.functions.containsKey(functionName) ?
                    new ResponseEntity<>(context.functions.get(functionName), HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.PUT)
    public ResponseEntity<Void> putFunction(@PathVariable String functionName,
                                            @RequestParam(value = "args") List<String> arguments,
                                            @RequestBody String functionExpression) {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            boolean success = checkName(functionName)
                    && arguments.stream().allMatch(MyController::checkName)
                    && context.setFunction(functionName, arguments, functionExpression);
            return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFunction(@PathVariable String functionName) {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            boolean success = context.functions.containsKey(functionName);
            context.functions.remove(functionName);
            return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/function/", method = RequestMethod.GET)
    public String[] getFunctions() {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            Set<String> functions = context.functions.keySet();
            return functions.toArray(new String[functions.size()]);
        }
    }

    @RequestMapping(value = "/eval/", method = RequestMethod.POST)
    public ResponseEntity<String> eval(@RequestBody String expression) {
        try (ContextProvider provider = getContextProvider()) {
            MyContext context = provider.context;
            try {
                double value = new SimpleCalculator().calculate(expression, context, null);
                return new ResponseEntity<>(String.valueOf(value), HttpStatus.OK);
            } catch (ParsingException | StackOverflowError e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @RequestMapping(value = "/clearContext", method = RequestMethod.POST)
    public void clearContext() {
        billingDao.clearContext();
    }

    @RequestMapping(value = "/clearAll", method = RequestMethod.POST)
    public void clearAll() {
        billingDao.clearAll();
    }
}
