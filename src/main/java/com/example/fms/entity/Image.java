package com.example.fms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "image")
public class Image extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "format", nullable = false)
    private String format;
    @Column(name = "url", nullable = false)
    private String url;
}

