package com.example.fms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "journal")
public class Journal extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "tablee")
    private String table;

    @Column(name = "action")
    private String action;

    @Column(name = "is_deleted", precision = 0)
    private boolean deleted;
}
