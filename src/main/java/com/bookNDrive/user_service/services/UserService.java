package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.models.User;
import com.bookNDrive.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUser(){
       var user = userRepository.findByMail("test@gmail.com").orElseThrow();
       return user;
    }

    public User createUser(){
        var user = new User();
        user.setFirstname("razzak");
        user.setLastname("khalfallah");
        user.setMail("test@gmail.com");
        user.setPassword("1234");

        return userRepository.save(user);
    }
}
