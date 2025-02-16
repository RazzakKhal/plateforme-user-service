package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.LoginDto;
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

    public User createUser(User user){
        // faire mapping entre Dto et vrai User

        return userRepository.save(user);
    }

    public User login(LoginDto loginDto){
        System.out.println("voici le mail : " + loginDto.getMail());
        var user = userRepository.findByMail(loginDto.getMail()).get();
        return user;
    }
}
