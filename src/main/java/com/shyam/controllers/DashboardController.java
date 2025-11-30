package com.shyam.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.repositories.AppointmentRepository;
import com.shyam.repositories.AppointmentRepository.TableCount;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping("/admin")
    public ResponseEntity<List<TableCount>> getAdminDetails() {
        List<TableCount> adminDetails = appointmentRepository.adminDetails();
        System.out.println(adminDetails);
        return ResponseEntity
                .ok()
                .body(adminDetails);
    }

    @GetMapping("/doctor/{doctor-id}")
    public ResponseEntity<List<TableCount>> getDoctorDetails(
        @PathVariable("doctor-id") long doctorId
    ) {
        List<TableCount> doctorDetails = appointmentRepository.doctorDetails(doctorId);
        System.out.println(doctorDetails);
        return ResponseEntity
                .ok()
                .body(doctorDetails);
    }
    
    @GetMapping("/lab")
    public ResponseEntity<List<TableCount>> getLabDetails() {
        List<TableCount> labDetails = appointmentRepository.labDetails();
        System.out.println(labDetails);
        return ResponseEntity
                .ok()
                .body(labDetails);
    }
    
    @GetMapping("/patient/{patient-id}")
    public ResponseEntity<List<TableCount>> getPatientDetails(
        @PathVariable("patient-id") long patientId
    ) {
        List<TableCount> patientDetails = appointmentRepository.patientDetails(patientId);
        System.out.println(patientDetails);
        return ResponseEntity
                .ok()
                .body(patientDetails);
    }
    
}
