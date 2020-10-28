package com.example.fms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "staff")
public class Staff extends BaseEntity{

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "staff_department", joinColumns={
			@JoinColumn(name = "staff_id") }, inverseJoinColumns = {
			@JoinColumn(name = "department_id") })
	private List<Department> departments;

	@Column(name = "position")
	private String position;

	@Column(name = "salary")
	private BigDecimal salary;

	@Column(name = "date")
	private Locale date;	//Дата начала работы сотрудника

	@Column(name = "accepted")
	private BigDecimal accepted;
}