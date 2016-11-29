package ru.mipt.java2016.homework.g594.sharuev.task4;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/variable")
public class Rest {

    @RequestMapping(method = RequestMethod.GET, path = "/{variableName}")
    public Variable getVariable(@PathVariable String variableName) {
        return new Variable(variableName, 42);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{variableName}")
    public String postVariable(@PathVariable String variableName, @RequestBody String body) {
        return "Olololol";
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{variableName}")
    public Variable deleteVariable(@PathVariable String variableName) {
        return new Variable(variableName, 42);
    }

}
