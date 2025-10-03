package com.cropdeal.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "farmer")
@Data
public class Farmer {
    @Id
    private Long id;

    private Integer userId;
}
