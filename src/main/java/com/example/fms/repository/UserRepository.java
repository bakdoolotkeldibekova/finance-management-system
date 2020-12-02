package com.example.fms.repository;

import com.example.fms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByActivationCode(String code);
    User findByEmail(String email);
    User findByEmailAndActive(String email, Boolean active);
    List<User> findAllByOrderByDateCreatedDesc();
    List<User> findAllByActive(boolean isActive);
    List<User> findAllByNameContainingIgnoringCase(String name);
    List<User> findAllBySurnameContainingIgnoringCase(String surname);
    List<User> findAllByPositionContainingIgnoringCase(String position);
    List<User> findAllByDateCreatedAfter(LocalDateTime after);
    List<User> findAllByDateCreatedBefore(LocalDateTime before);
}
