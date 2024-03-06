package maqlu.maqlulibrary.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Json {
    @GetMapping("/json")
    @ResponseBody
    public String jsonReturn(){
        return "{\"message\": \"Hello, JSON!\"}";
    }
}
