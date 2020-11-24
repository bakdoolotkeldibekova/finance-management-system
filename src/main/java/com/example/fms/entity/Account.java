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
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "account")
@JsonInclude
@SQLDelete(sql = "UPDATE account SET is_deleted=true WHERE id=?")
@FilterDef(
        name = "deletedAccountFilter",
        parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(
        name = "deletedAccountFilter",
        condition = "is_deleted = :isDeleted"
)
public class Account extends BaseEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "balance")
    private BigDecimal balance;

}
