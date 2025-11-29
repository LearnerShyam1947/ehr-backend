package com.shyam.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shyam.dto.response.AppointmentResponse;
import com.shyam.entities.AppointmentEntity;
import com.shyam.entities.UserEntity;
import com.shyam.enums.AppointmentStatus;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.services.AppointmentService;
import com.shyam.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/doctor")
@Tag(
    name = "Doctor Controller",
    description = "All end points useful for Doctors" 
)
public class DoctorController {
    
    private final AppointmentService appointmentService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllDoctors() {
        return ResponseEntity.ok(userService.getDoctors());
    }

    @PostMapping("/appointment/{appointment-id}/accept")
    public ResponseEntity<AppointmentEntity> accept(
        @PathVariable("appointment-id") long appointmentId
    ) throws RequestedEntityNotFoundException {
        // UserEntity currentUser = userService.getCurrentUser();
        AppointmentEntity appointment = appointmentService.getAppointment(appointmentId);

        // if (currentUser.getId() != appointment.getDoctorId()) 
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you can't access other user appointment");

        if (appointment.getStatus().name().equals(AppointmentStatus.EXPIRED.name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The appointment schedule is complete");

        if (appointment.getStatus().name().equals(AppointmentStatus.CANCELED.name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already canceled the appointment can't preform the request action");
        
        return ResponseEntity
                .status(HttpStatus.ACCEPTED.value())
                .body(appointmentService.changeStatus(appointment, AppointmentStatus.ACCEPTED));
        
    }
    
    @PostMapping("/appointment/{appointment-id}/reject")
    public ResponseEntity<AppointmentEntity> reject(
        @PathVariable("appointment-id") long appointmentId,
        @RequestParam(required = true) String reason
    ) throws RequestedEntityNotFoundException {
        // UserEntity currentUser = userService.getCurrentUser();
        AppointmentEntity appointment = appointmentService.getAppointment(appointmentId);

        // if (currentUser.getId() != appointment.getDoctorId()) 
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you can't access other user appointment");

        if (appointment.getStatus().name().equals(AppointmentStatus.EXPIRED.name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The appointment schedule is complete");
    
        if (appointment.getStatus().name().equals(AppointmentStatus.CANCELED.name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already canceled the appointment can't preform the request action");
            
        
        return ResponseEntity
                .status(HttpStatus.ACCEPTED.value())
                .body(appointmentService.changeStatus(appointment, AppointmentStatus.REJECTED, reason));
        
    }

    @SuppressWarnings("null")
    @GetMapping("/appointment/{user-id}")
    public ResponseEntity<?> getAllAppointments(
        @PathVariable("user-id") long userId
    ) {
        UserEntity currentUser = userService.getUserById(userId).get();
        List<AppointmentEntity> doctorAppointments = appointmentService.getDoctorAppointment(userId);
        Set<Long> patientIds = doctorAppointments.stream().map(a -> a.getPatientId()).collect(Collectors.toSet());
        List<UserEntity> patientRecords = userService.getAll(patientIds);

        List<AppointmentResponse> appointments = doctorAppointments.stream().map(a -> {
            AppointmentResponse response = new AppointmentResponse();
            BeanUtils.copyProperties(a, response);
            response.setStatus(a.getStatus().name());
            response.setDoctor(currentUser);
            response.setPatient(
                patientRecords.stream()
                    .filter(p -> p.getId() == a.getPatientId())
                    .findFirst().get()
            );
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(appointments);
    }

    // prescription

}
