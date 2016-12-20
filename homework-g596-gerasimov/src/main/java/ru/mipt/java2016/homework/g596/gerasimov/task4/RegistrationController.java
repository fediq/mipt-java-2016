package ru.mipt.java2016.homework.g596.gerasimov.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by geras-artem on 18.12.16.
 */

@RestController
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired private BillingDao billingDao;

    @RequestMapping(path = "/registration/{username}", method = RequestMethod.POST)
    public String registration(@PathVariable String username,
            @RequestParam(value = "password") String password) {
        LOG.debug("Adding request for user: " + username);
        if (!billingDao.addUser(username, password)) {
            LOG.trace("Error");
            return "User already exists\n";
        }
        LOG.trace("Success");
        return "User successfully added\nUsername: " + username + "\nPassword: " + password + "\n";
    }
}
