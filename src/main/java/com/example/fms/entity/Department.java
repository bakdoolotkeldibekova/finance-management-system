package com.example.fms.entity;

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
@Table(name = "department")
@JsonInclude
@SQLDelete(sql = "UPDATE department SET is_deleted=true WHERE id=?")
@FilterDef(
		name = "deletedDepartmentFilter",
		parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(
		name = "deletedDepartmentFilter",
		condition = "is_deleted = :isDeleted"
)
public class Department extends BaseEntity{

	@Column(name = "name", nullable = false)
	private String name;

//	@ManyToMany(mappedBy = "staff") не нужно
//	private List<Staff> staffList;

//	@ManyToMany(mappedBy = "users") не нужно
//	private List<User> users;
}