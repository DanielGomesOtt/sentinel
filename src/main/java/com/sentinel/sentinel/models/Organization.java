package com.sentinel.sentinel.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int status;

    public Organization(){}

    public Organization(@NotNull Long id, @NotNull String name, @NotNull int status) {
        this.id = id;
        this.name = name;
        this.status = 1;
    }

    public Organization(@NotNull String name, @NotNull int status) {
        this.name = name;
        this.status = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
