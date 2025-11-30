package com.shyam.entities;

import com.shyam.enums.HospitalStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hospitals")
public class HospitalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HospitalStatus status;

    private String name;
    private String city;
    private String state;
    private String email;
    private String address;
    private String zipCode;
    private String country;
    private String website;
    private int totalBeds;
    private String phoneNumber; 
    private int availableBeds;
    private int establishedYear;
    private boolean emergencyServices;
    private boolean ambulanceServices;
}
