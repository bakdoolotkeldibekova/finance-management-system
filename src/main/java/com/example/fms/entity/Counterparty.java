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
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "counterparty")
@JsonInclude
@SQLDelete(sql = "UPDATE counterparty SET deleted=true WHERE id=?")
@FilterDef(
        name = "deletedCounterpartyFilter",
        parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(
        name = "deletedCounterpartyFilter",
        condition = "deleted = :isDeleted"
)
public class Counterparty extends BaseEntity {

    @Column(name = "name", length = 25, nullable = false, unique = true)
    private String name;
}
