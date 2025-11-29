package com.shyam.dto.response;

import java.util.Date;

import com.shyam.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private long id;
    private Date date;
    private String slot;
    private String status;
    private String reason;
    private UserEntity doctor;
    private UserEntity patient;
    private String rejectionReason;
}