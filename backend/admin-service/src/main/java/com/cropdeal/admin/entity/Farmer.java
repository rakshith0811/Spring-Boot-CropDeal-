package com.cropdeal.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "farmer")
@Data
public class Farmer {
    @Id
    private Long id;
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Integer userId;
}
