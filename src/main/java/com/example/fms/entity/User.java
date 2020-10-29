package com.example.fms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User extends BaseEntity{

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "user_department", joinColumns={
//            @JoinColumn(name = "user_id") }, inverseJoinColumns = {
//            @JoinColumn(name = "department_id") })
//    private List<Department> departments;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns={
            @JoinColumn(name = "user_id") }, inverseJoinColumns = {
            @JoinColumn(name = "role_id") })
    private List<Role> roles;

    @Column(name = "position")
    private String position;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "activation_code")
    private String activationCode;

}
