package com.cropdeal.farmer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean active;

    private String address;

    @Column(name = "mobile_number")
    private String mobileNumber;

    private String password;

    private String role;

    private String username;

  //  @Column(name = "federated_id")  // example for email or Facebook federated login id
    //private String federatedId;

}
