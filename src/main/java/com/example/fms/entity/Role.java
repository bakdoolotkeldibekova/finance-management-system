package com.example.fms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {

    @JsonIgnore
    @Column(name = "is_deleted", precision = 0)
    private boolean deleted;

    @Column(name = "name", length = 50)
    private String name;

    public Role(String name){
        this.name = name;
    }

    @Override//for jwt
    public String getAuthority() {
        return getName();
    }
}
