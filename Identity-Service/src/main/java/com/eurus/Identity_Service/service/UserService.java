package com.eurus.Identity_Service.service;


import com.eurus.Identity_Service.dto.request.UserCreationRequest;
import com.eurus.Identity_Service.dto.request.UserDeleteRequest;
import com.eurus.Identity_Service.dto.request.UserUpdateRequest;
import com.eurus.Identity_Service.entity.User;
import com.eurus.Identity_Service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Danh dau day la lop Service Logic

public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request){
        //1. Tao User moi
        User user = new User();

        //2. Chep du lie tu Request sang User
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        //3. Goi Repository luu xuong DB
        return userRepository.save(user);

    }
    public List<User> getUsers(){
        return userRepository.findAll();
    }
    public User getUser(String id){
        return userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));
    }

    //update User
    public User updateUser(String userId, UserUpdateRequest request){
        User user = getUser(userId); // Tan dung getUser o tren
        // Cap nhat thong tin tu request vao user entity
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        //Luu xuong DB
        return  userRepository.save(user);
    }

    //delete User
    public void deleteUser(String userId){

        userRepository.deleteById(userId);

        // Vi day la kieu void nen khong return ve gi ca
    }

}
