package com.cropdeal.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dealer")
@Data
public class Dealer {
    @Id
    private Long id;

    private Integer userId;
}
