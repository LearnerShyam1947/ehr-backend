package com.shyam.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shyam.dto.request.PatientRecordAccessRequest;
import com.shyam.dto.response.AppointmentResponse;
import com.shyam.dto.response.PatientAccessResponse;
import com.shyam.entities.AppointmentEntity;
import com.shyam.entities.ReportEntity;
import com.shyam.entities.UserEntity;
import com.shyam.enums.AppointmentStatus;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.services.AppointmentService;
import com.shyam.services.PatientService;
import com.shyam.services.PatientService.ReportResponse;
import com.shyam.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patient")
@Tag(
    name = "Patient Controller",
    description = "All end points useful for patients" 
)
public class PatientController {

    private final UserService userService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    
    @SuppressWarnings("null")
    @GetMapping("/appointment/{user-id}")
    public ResponseEntity<?> appointments(
        @PathVariable("user-id") long userId
    ) {
        // UserEntity currentUser = userService.getCurrentUser();
        UserEntity currentUser = userService.getUserById(userId).get();
        List<AppointmentEntity> patientAppointments = appointmentService.getPatientAppointment(userId);
        Set<Long> doctorIds = patientAppointments.stream().map(a -> a.getDoctorId()).collect(Collectors.toSet());
        List<UserEntity> doctorRecords = userService.getAll(doctorIds);

        List<AppointmentResponse> appointments = patientAppointments.stream().map(a -> {
            AppointmentResponse response = new AppointmentResponse();
            BeanUtils.copyProperties(a, response);
            response.setStatus(a.getStatus().name());
            response.setPatient(currentUser);
            response.setDoctor(
                doctorRecords.stream()
                    .filter(p -> p.getId() == a.getDoctorId())
                    .findFirst().get()
            );
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(appointments);
    }

    @GetMapping("/appointment/{appointment-id}/cancel")
    public ResponseEntity<AppointmentEntity> reject(
        @PathVariable("appointment-id") long appointmentId
    ) throws RequestedEntityNotFoundException {
        // UserEntity currentUser = userService.getCurrentUser();
        AppointmentEntity appointment = appointmentService.getAppointment(appointmentId);

        // if (currentUser.getId() != appointment.getPatientId()) 
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you can't access other user appointment");

        if (appointment.getStatus().name().equals(AppointmentStatus.EXPIRED.name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The appointment schedule is complete");
    
        if (appointment.getStatus().name().equals(AppointmentStatus.REJECTED.name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already rejected the appointment can't preform the request action");
            
        
        return ResponseEntity
                .status(HttpStatus.ACCEPTED.value())
                .body(appointmentService.changeStatus(appointment, AppointmentStatus.CANCELED));
        
    }

    @GetMapping("/access-list")
    public ResponseEntity<?> accessList() throws RequestedEntityNotFoundException {

        Set<PatientAccessResponse> accessList = patientService.getAccessList();
        
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(accessList);
    }
    
    @PostMapping("/access-list/{patient-id}/add")
    public ResponseEntity<Map<String, String>> addToAccessList(
        @Valid @RequestBody PatientRecordAccessRequest userAccessRequest,
        @PathVariable("patient-id") int patientId
    ) throws RequestedEntityNotFoundException {

        String userToAccessList = patientService.addUserToAccessList(patientId, userAccessRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(Map.of("message", "user access " + userToAccessList + " successfully"));
    }
    
    @PostMapping("/access-list/{patient-id}/remove")
    public ResponseEntity<Map<String, String>> removeToAccessList(
        @PathVariable("patient-id") long patientId,
        @RequestBody UserIdRequest request
    ) throws RequestedEntityNotFoundException {
        patientService.removeUserToAccessList(patientId, request.userId);
        
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(Map.of("message", "user access removed successfully"));
    }
    
    @PostMapping("/access-list/{patient-id}/has-access")
    public ResponseEntity<Map<String, Object>> hasAccess(
        @PathVariable("patient-id") long patientId,
        @RequestBody UserIdRequest request
    ) throws RequestedEntityNotFoundException {
        boolean canAccess = patientService.hasAccessToRecord(patientId, request.userId);
        Set<ReportResponse> reportResponses = Set.of();

        // todo: add user details if canAccess is not false
        if (canAccess) {
            reportResponses = patientService.getPatientReports(patientId);
        }
        
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(Map.of(
                    "accessStatus", canAccess,
                    "records", reportResponses
                ));
    }

    @PostMapping("/reports/request")
    public ResponseEntity<Map<String, Object>> requestForReport(
        @RequestBody ReportRequest reportRequest
    ) {
        ReportEntity report = patientService.requestForReport(reportRequest.reportName);
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(Map.of(
                    "message", "Report requested successfully",
                    "report", report
                ));
    }

    @GetMapping("/reports")
    public ResponseEntity<Set<ReportResponse>> getPatientReports() {
        Set<ReportResponse> patientReports = patientService.getPatientReports();

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(patientReports);
    }

    public record ReportRequest(String reportName) { }
    public record UserIdRequest(long userId) { }

}
