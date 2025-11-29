package com.shyam.entities;

import java.util.Date;

import com.shyam.enums.MFAType;
import com.shyam.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date expireTime;

    @Enumerated(value = EnumType.STRING)
    private MFAType mfaType;

    private String otp;

    private String email;
    private String token;
    private String secret;
    private String username;
    private String resumeUrl;
    private double experience;
    private String phoneNumber;
    private boolean mfaEnabled;
    private String passwordHash;
    private String specialization;
}
