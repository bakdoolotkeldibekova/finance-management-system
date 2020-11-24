package com.example.fms.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "staff")
@JsonInclude
@SQLDelete(sql = "UPDATE staff SET is_deleted=true WHERE id=?")
@FilterDef(
		name = "deletedStaffFilter",
		parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(
		name = "deletedStaffFilter",
		condition = "is_deleted = :isDeleted"
)
public class Staff extends BaseEntity {

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "staff_department", joinColumns = {
			@JoinColumn(name = "staff_id")}, inverseJoinColumns = {
			@JoinColumn(name = "department_id")})
	private List<Department> departments;

	@Column(name = "position")
	private String position;

	@Column(name = "salary")
	private BigDecimal salary;

	@Column(name = "date")
	private LocalDateTime date;    //Дата начала работы сотрудника

	@Column(name = "accepted")
	private BigDecimal accepted;

}