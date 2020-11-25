package com.example.fms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
@JsonInclude
public class User extends BaseEntity{

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
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

//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinTable(name = "user_role", joinColumns={
//            @JoinColumn(name = "user_id") }, inverseJoinColumns = {
//            @JoinColumn(name = "role_id") })

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "position")
    private String position;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "activation_code")
    private String activationCode;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

}
