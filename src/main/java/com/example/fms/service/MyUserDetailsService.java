package com.example.fms.service;

import com.example.fms.entity.Role;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//for working with jwt

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.example.fms.entity.User userAccount = userAccountRepository.findByEmailAndActive(email, true);
        //System.out.println(userAccount);
        List<Role> roles = new ArrayList<>();
        roles.add(userAccount.getRole());
        return new User(userAccount.getEmail(), userAccount.getPassword(), roles);
    }
}