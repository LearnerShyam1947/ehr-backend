package com.shyam.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientAccessResponse {
    
    private long id;
    private Date expire;
    private String role;
    private String email;
    private String address;
    private String username;
    private String resumeUrl;
    private double experience;
    private String phoneNumber;
    private String specialization;

}
