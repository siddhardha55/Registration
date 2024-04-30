package com.example.register.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.register.registration.model.Users;
import com.example.register.registration.repository.UserJpaRepository;
import com.example.register.registration.repository.UserRepository;


@Service
public class UserJpaService implements UserRepository {

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Override
    public Users addUser(Users user) {
        userJpaRepository.save(user);
        return user;

    }
    @Override
    public Users updatePassword() {
        return null;
    }

}