package com.eurus.hello_spring_boot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello(){
        return "Hello World! I will be a Devops Engineer.";

    }
    @GetMapping("Goodbye")
    public String sayGoodbye(){
        return "GoodBye!";
    }
}
