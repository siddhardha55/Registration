package com.example.register.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.register.registration.model.Users;

@Repository
public interface UserJpaRepository extends JpaRepository<Users, Long> {
    boolean existsByEmailId(String email);
    Users findByEmailId(String emailId);
    Users findByResetToken(String token);

}