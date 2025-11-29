package com.shyam.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.dto.request.AppointmentRequest;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.services.AppointmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/appointment")
@Tag(
    name = "Appointment Controller",
    description = "All end points useful for user to manage their Appointments" 
)
public class AppointmentController {

    private final AppointmentService appointmentService;
    
    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        System.out.println("I am here....");
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(appointmentService.getAllAppointments());
    }
    
    @GetMapping("/{appointment-id}")
    public ResponseEntity<?> getAppointment(
        @PathVariable("appointment-id") int appointmentId
    ) throws RequestedEntityNotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(appointmentService.getAppointment(appointmentId));
    }

    @PostMapping
    public ResponseEntity<?> addAppointments(
        @Valid @RequestBody AppointmentRequest request
    ) throws EntityAlreadyExistsException {
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(appointmentService.addAppointment(request));
    }

}
