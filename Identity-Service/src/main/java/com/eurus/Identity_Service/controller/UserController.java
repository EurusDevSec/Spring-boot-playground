package com.eurus.Identity_Service.controller;


import com.eurus.Identity_Service.dto.request.UserCreationRequest;
import com.eurus.Identity_Service.entity.User;
import com.eurus.Identity_Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")


public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping // Tao moi user
    User createUser(@RequestBody UserCreationRequest request){
        return userService.createUser(request);
    }
    @GetMapping // Lay danh sach User
    List<User> getUsers(){
        return userService.getUsers();
    }
    @GetMapping("/{userId}") //Lay 1 user theo ID
    User getUser(@PathVariable("userId") String userId){
        return  userService.getUser(userId);
    }
    @PutMapping("/{userId}")

}
