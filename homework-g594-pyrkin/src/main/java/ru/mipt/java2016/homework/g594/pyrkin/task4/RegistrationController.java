package ru.mipt.java2016.homework.g594.pyrkin.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by randan on 12/17/16.
 */

@RestController
public class RegistrationController {

    @Autowired
    private BillingDao billingDao;

    @RequestMapping(path = "/registration/{username}", method = RequestMethod.POST,
            consumes = "text/plain", produces = "text/plain")
    public String registration(@PathVariable String username, @RequestBody String password) {
        if (billingDao.addUser(username, password)) {
            return "User created!\nUsername: " + username + "\nPassword: " + password + "\n";
        }
        return "User already exists\n";
    }
}
