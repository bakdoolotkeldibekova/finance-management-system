package com.example.fms.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "account")
@JsonInclude
public class Account extends BaseEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "balance")
    private BigDecimal balance;

}
