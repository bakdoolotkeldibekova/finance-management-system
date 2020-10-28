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
@Table(name = "department")
public class Department extends BaseEntity{

	@Column(name = "name", nullable = false)
	private String name;

//	@ManyToMany(mappedBy = "staff") не нужно
//	private List<Staff> staffList;

//	@ManyToMany(mappedBy = "users") не нужно
//	private List<User> users;
}