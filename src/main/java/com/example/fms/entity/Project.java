package com.example.fms.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "project")
@JsonInclude
@SQLDelete(sql = "UPDATE project SET deleted=true WHERE id=?")
@FilterDef(
        name = "deletedProjectFilter",
        parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(
        name = "deletedProjectFilter",
        condition = "deleted = :isDeleted"
)
public class Project extends BaseEntity{

    @Column(name = "name", length = 50, nullable = false)
    private String name;
}
