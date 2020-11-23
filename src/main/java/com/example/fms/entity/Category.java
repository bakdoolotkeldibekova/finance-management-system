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

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "category")
@JsonInclude
@SQLDelete(sql = "UPDATE category SET deleted=true WHERE id=?")
@FilterDef(
		name = "deletedCategoryFilter",
		parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(
		name = "deletedCategoryFilter",
		condition = "deleted = :isDeleted"
)
public class Category extends BaseEntity{

	@Column(name = "name", nullable = false)
	private String name;

}