package com.example.fms.repository;

import com.example.fms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByActivationCode(String code);
    User findByEmail(String email);
    User findByEmailAndActive(String email, Boolean active);
}
