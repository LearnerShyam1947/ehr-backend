package com.shyam.services;

import java.sql.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shyam.entities.ReportEntity;
import com.shyam.enums.ReportStatus;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.repositories.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabTechnicianService {

    private final UserService userService;
    private final ReportRepository reportRepository;

    public ReportEntity acceptReport(long reportId) throws RequestedEntityNotFoundException {
        ReportEntity report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RequestedEntityNotFoundException("No Report Found with id : " + reportId));

        if (!ReportStatus.REQUESTED.name().equals(report.getReportStatus().name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The report request already accepted by other person");

        long labTechnicianId = userService.getCurrentUser().getId();

        report.setDoctorId(labTechnicianId);
        report.setReportStatus(ReportStatus.STARTED);
        report.setStartedAt(new Date(System.currentTimeMillis()));
        
        return reportRepository.save(report);
    }

    public ReportEntity uploadReport(long reportId, String reportUrl) throws RequestedEntityNotFoundException {
        ReportEntity report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RequestedEntityNotFoundException("No Report Found with id : " + reportId));

        System.out.println("\n i am here " + reportUrl);

         if (!ReportStatus.STARTED.name().equals(report.getReportStatus().name())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The report request was not accepted yet");

        report.setReportUrl(reportUrl);
        report.setReportStatus(ReportStatus.COMPLETED);
        report.setCompletedAt(new Date(System.currentTimeMillis()));
        
        return reportRepository.save(report);
    }

    public List<ReportEntity> getMyReports() {
        
        long labTechnicianId = userService.getCurrentUser().getId();
        
        return reportRepository.findByDoctorId(labTechnicianId);
        
    }
    
}
