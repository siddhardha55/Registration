package com.example.register.registration.repository;

import com.example.register.registration.model.Users;

public interface UserRepository {
    Users addUser(Users user);
    Users updatePassword();
}