package com.eurus.hello_spring_boot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "World") String name){
        return "Hello " + name + "! Toi se tro thanh DevOps Engineer!";

    }
    @GetMapping("Goodbye")
    public String sayGoodbye(){
        return "GoodBye!";
    }
}
