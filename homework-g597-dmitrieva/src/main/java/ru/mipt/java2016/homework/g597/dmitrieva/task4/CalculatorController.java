package ru.mipt.java2016.homework.g597.dmitrieva.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private StringCalculator calculator;

    @Autowired
    public BillingDao billingDao;

    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "DRATUTI\n";
    }

    @RequestMapping(path = "/", method = RequestMethod.GET, produces = "text/html")
    public String main(@RequestParam(required = false) String name) {
        if (name == null) {
            name = "world";
        }
        return "<html>" + "<head><title>IrinaPsinaApp</title></head>" + "<body><h1>Hello, " + name + "!</h1></body>" + "</html>";
    }

    /*
     * Получить выражение, обозначенное переменной с указанным именем.
     */
    @RequestMapping(path = "/variable/{nameOfVariable}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String nameOfVariable) {
        String ourName = authentication.getName();
        Double result = billingDao.getVariable(ourName, nameOfVariable);
        return nameOfVariable + " = " + result + "\n";
    }

    /*
    * Получить список имен всех переменных в сервисе.
    */
    @RequestMapping(path = "/variable", method = RequestMethod.GET, produces = "text/plain")
    public String getVariables(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        Map<String, Double> result = billingDao.getVariables(username);
        return String.join(", ", result.keySet()) + "\n" + "";
    }

    /*
     * Удалить переменную с заданным именем.
     */
    @RequestMapping(path = "/variable/{nameOfVariable}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteVariable(Authentication authentication, @PathVariable String nameOfVariable) {
        String username = authentication.getName();
        boolean deleteSucceeded = billingDao.deleteVariable(username, nameOfVariable);
        if (deleteSucceeded) {
            return nameOfVariable + " has been deleted\n";
        } else {
            return nameOfVariable + " does not exists\n";
        }
    }

    /*
     * Присвоить переменной новое выражение.
     */

    @RequestMapping(path = "/variable/{varName}", method = RequestMethod.PUT, consumes = "*/*;charset=UTF-8", produces = "text/plain")
    public String addVariable(Authentication authentication, @PathVariable String varName, @RequestBody String valueOfVariable) throws ParsingException {
        String username = authentication.getName();
        billingDao.addVariable(username, varName, Double.parseDouble(valueOfVariable));
        return "Variable " + varName + " has been added\n";
    }


    /*
     * Получить выражение, обозначенное функцией с указанным именем.
     * Также возвращает список аргументов функции. Нельзя получить выражение для предопределенных функций.
     */

    @RequestMapping(path = "/function/{nameOfFunction}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunction(Authentication authentication, @PathVariable String nameOfFunction) {
         String username = authentication.getName();
        Function result = billingDao.getFunction(username, nameOfFunction);
        return nameOfFunction + "(" + String.join(", ", result.getArguments()) + ")" + " = " + result.getExpression() + "\n";
    }

     /*
     * Удалить функцию с заданным именем. Предопределенные функции нельзя удалить.
     */
    @RequestMapping(path = "/function/{nameOfFunction}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteFunction(Authentication authentication, @PathVariable String nameOfFunction) {
        String username = authentication.getName();
        boolean success = billingDao.deleteFunction(username, nameOfFunction);
        if (success) {
            return nameOfFunction + " has been deleted\n";
        } else {
            return nameOfFunction + " does not exists\n";
        }
    }

    /*
    * Присвоить функции с заданным именем следующее выражение и список аргументов.
    * Предопределенные функции нельзя изменить.
    */

    //@RequestMapping(path = "/function/{nameOfFunction}", method = RequestMethod.PUT, consumes = "*/*;charset=UTF-8", produces = "text/plain")
    /*public String addFunction(Authentication authentication, @PathVariable String nameOfFunction,
                              @RequestParam(value = "args") String args,
                              @RequestBody String expression) {
        System.out.println("HELLO!!!!!!!!");
        String username = authentication.getName();
        List<String> arguments = Arrays.asList(args.split(","));
        billingDao.addFunction(username, nameOfFunction, arguments, expression);
        return "Function" + nameOfFunction + " has been added\n";
    } */

    @RequestMapping(path = "/function/{name}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String addFunction(Authentication authentication, @PathVariable String name,
                              @RequestParam(value = "args") String args,
                              @RequestBody String expression)
            throws ParsingException {
        String username = authentication.getName();
        List<String> arguments = Arrays.asList(args.split(","));
        billingDao.addFunction(username, name, arguments, expression);
        return "Function" + name + "has been added\n";
    }

    /*
     * Получить список имен всех пользовательских функций в сервисе.
     */
    @RequestMapping(path = "/function", method = RequestMethod.GET, produces = "text/plain")
    public String getFunctions(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        TreeMap<String, Function> map1 = billingDao.getFunctions("username");
        TreeMap<String, Function> map2 = billingDao.getFunctions(username);
        TreeMap<String, Function> result = new TreeMap<String, Function>();
        result.putAll(map1);
        result.putAll(map2);
        return String.join(", ", result.keySet()) + "\n";
    }


    /*
     * Рассчитать значение выражения.
     */
    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "*/*;charset=UTF-8", produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expression) throws ParsingException {
        try {
            LOG.debug("Evaluation request: [" + expression + "]");
            TreeMap<String, Function> AllFunctionsMap = billingDao.getFunctions(authentication.getName());
            int beginIndexOfVariable = 0;
            int endIndexOfVariable = 0;
            boolean isReadingVariable = false;
            for (int i = 0; i < expression.length(); i++) {
                // Нашли что-то, что начинается с буквы -- возможно, это переменная
                if (Character.isLetter(expression.charAt(i)) && !isReadingVariable) {
                    beginIndexOfVariable = i;
                    isReadingVariable = true;
                    continue;
                }
                // находимся в процессе чтения переменной (если это она)
                if ((Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_') && isReadingVariable) {
                    endIndexOfVariable = i;
                    continue;
                }
                if (!(Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_') && isReadingVariable) {
                    isReadingVariable = false;
                    String variable = expression.substring(beginIndexOfVariable, endIndexOfVariable + 1);
                    // Если мы нашли не переменную, а какую-то функцию, то ничего с ней делать не хотим
                    if (AllFunctionsMap.containsKey(variable)) {
                        continue;
                    }
                    // Получаем значение переменной
                    String value = billingDao.getVariable(authentication.getName(), variable).toString();
                    // Заменяем ее первое вхождение на значение
                    expression = expression.replaceFirst(variable, value);
                    // Дальше обновляем счетчик и снова ищем какую-нибудь переменную
                    i = 0;
                }
            }
            double result = calculator.calculateWithFunctions(expression, AllFunctionsMap);
            //double result = calculator.calculate(expression);
            LOG.trace("Result: " + result);
            return Double.toString(result) + "\n";
        } catch (ParsingException e) {
            throw new IllegalArgumentException("OSTANOVIS', POKA OSTANOVKA NE BUDET POSLEDNEY\n");
        }
    }
}
