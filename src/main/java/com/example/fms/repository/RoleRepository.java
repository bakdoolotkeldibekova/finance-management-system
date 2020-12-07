package com.example.fms.repository;

import com.example.fms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByOrderByDateCreatedDesc();
    Role findByName(String name);
}
