package com.shyam.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.entities.ReportEntity;
import com.shyam.entities.UserEntity;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.services.LabTechnicianService;
import com.shyam.services.PatientService.LabDetails;
import com.shyam.services.PatientService.ReportResponse;
import com.shyam.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lab-technician")
public class LabTechnicianController {

    private final UserService userService;
    private final LabTechnicianService labTechnicianService;

    @GetMapping("/reports")
    public ResponseEntity<?> getMyReports() {

        List<ReportEntity> myReports = labTechnicianService.getMyReports();

        Set<ReportResponse> reportResponse = myReports.stream()
            .map(this::reportEntityToResponse)
            .collect(Collectors.toSet());

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(reportResponse);
    }
    
    @PostMapping("/reports/{report-id}/accept")
    public ResponseEntity<?> acceptReport(
        @PathVariable("report-id") long reportId
    ) throws RequestedEntityNotFoundException {

        ReportEntity acceptedReport = labTechnicianService.acceptReport(reportId);

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(Map.of(
                    "message", "report request accepted successfully",
                    "report", acceptedReport
                ));
    }

    @PostMapping("/reports/{report-id}/upload")
    public ResponseEntity<?> uploadReport(
        @PathVariable("report-id") long reportId,
        @RequestBody ReportUrlRequest request
    ) throws RequestedEntityNotFoundException {

        ReportEntity updatedReport = labTechnicianService.uploadReport(reportId, request.reportUrl);

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(Map.of(
                    "message", "report request completed successfully",
                    "report", updatedReport
                ));
    }

    public ReportResponse reportEntityToResponse(ReportEntity entity) {
        UserEntity userById = userService
                                .getUserById(entity.getDoctorId())
                                .orElse(null);

        LabDetails details = userById == null ? null : new LabDetails(userById.getId(), userById.getUsername());

        ReportResponse response = new ReportResponse(
            entity.getId(), 
            entity.getReportStatus(), 
            entity.getRequestAt(), 
            entity.getStartedAt(), 
            entity.getCompletedAt(), 
            entity.getDoctorId(), 
            entity.getPatientId(), 
            entity.getReportUrl(), 
            entity.getReportName(), 
            details
        );

        return response;
    }

    public record ReportUrlRequest(String reportUrl) { }

}
