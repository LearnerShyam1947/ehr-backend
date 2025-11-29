package com.shyam.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.shyam.dto.request.PatientRecordAccessRequest;
import com.shyam.dto.response.PatientAccessResponse;
import com.shyam.entities.PatientAccessListEntity;
import com.shyam.entities.PatientRecordEntity;
import com.shyam.entities.ReportEntity;
import com.shyam.entities.UserEntity;
import com.shyam.enums.ReportStatus;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.repositories.PatientAccessRepository;
import com.shyam.repositories.PatientRecordRepository;
import com.shyam.repositories.ReportRepository;
import com.shyam.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientAccessRepository patientAccessRepository;
    private final PatientRecordRepository patientRecordRepository;
    private final ReportRepository reportRepository;
    private final UserService userService;

    public PatientRecordEntity getPatientRecord(long id) throws RequestedEntityNotFoundException {
        PatientRecordEntity patientRecord = patientRecordRepository.findByPatientId(id)
            .orElseThrow(() -> new RequestedEntityNotFoundException("No Patient Record Found with id : " + id));

        return patientRecord;
    }

    public Set<PatientAccessResponse> getAccessList() throws RequestedEntityNotFoundException {
        long id = userService.getCurrentUser().getId();

        List<PatientAccessListEntity> accessList = patientAccessRepository.findByPatientId(id);
        System.out.println("in service: "+ accessList);
        
        Set<PatientAccessResponse> accessListWithUserDetails = accessList.stream()
                .map(entity -> {
                    try {
                        return toPatientAccessResponse(entity);
                    } catch (RequestedEntityNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toSet());
        
        return accessListWithUserDetails;
    }

    public String addUserToAccessList(long patientId, PatientRecordAccessRequest userAccessRequest) throws RequestedEntityNotFoundException {

        PatientAccessListEntity byUserIdAndPatientId = patientAccessRepository.findByUserIdAndPatientId(userAccessRequest.getUserId(), patientId);

        if (byUserIdAndPatientId == null) {
            PatientAccessListEntity newRecord = new PatientAccessListEntity();
            newRecord.setExpiration(getExpirationDate(userAccessRequest.getExpire()));
            newRecord.setPatientId(patientId);
            newRecord.setUserId(userAccessRequest.getUserId());
    
            patientAccessRepository.save(newRecord);

            return "granted";
        }

        byUserIdAndPatientId.setExpiration(getExpirationDate(userAccessRequest.getExpire()));
        patientAccessRepository.save(byUserIdAndPatientId);

        return "updated";

    }
    
    public void removeUserToAccessList(long patientId, long userId) throws RequestedEntityNotFoundException {

        PatientAccessListEntity toRemove = patientAccessRepository.findByUserIdAndPatientId(userId, patientId);

        if (toRemove != null) 
            patientAccessRepository.delete(toRemove);
    }
    
    public boolean hasAccessToRecord(long patientId, long userId) throws RequestedEntityNotFoundException {
        
        PatientAccessListEntity toCheckAccess = patientAccessRepository.findByUserIdAndPatientId(userId, patientId);
        
        if (toCheckAccess != null) {
            Date expiration = toCheckAccess.getExpiration();
            return expiration.after(new Date());    
        }

        return false;
    }

    @SuppressWarnings("null")
    private PatientAccessResponse toPatientAccessResponse(PatientAccessListEntity entity) throws RequestedEntityNotFoundException {

        PatientAccessResponse response = new PatientAccessResponse();

        UserEntity userEntity = userService.getUserById(entity.getUserId())
                                    .orElseThrow(() -> new RequestedEntityNotFoundException());

        BeanUtils.copyProperties(userEntity, response);
        response.setRole(userEntity.getRole().name());
        response.setExpire(entity.getExpiration());
        
        return response;
    }

    private Date getExpirationDate(String durationString) {
        if (durationString == null) {
            throw new IllegalArgumentException("durationString is null");
        }

        durationString = durationString.toLowerCase().trim();

        switch (durationString) {
            case "1day":
                return Utils.getAddedDate(1, Calendar.DAY_OF_MONTH);
            case "1week":
                return Utils.getAddedDate(1, Calendar.WEEK_OF_YEAR);
            case "1month":
                return Utils.getAddedDate(1, Calendar.MONTH);
            case "noexpire":
                return null;  // or you can return some sentinel, e.g. new Date(Long.MAX_VALUE)
            default:
                throw new IllegalArgumentException("Unsupported duration: " + durationString);
        }
    }

    public ReportEntity requestForReport(String reportName) {

        long patientId = userService.getCurrentUser().getId();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setPatientId(patientId);
        reportEntity.setReportName(reportName);
        reportEntity.setReportStatus(ReportStatus.REQUESTED);
        reportEntity.setRequestAt(new Date(System.currentTimeMillis()));

        return reportRepository.save(reportEntity);
    }

    public Set<ReportResponse> getPatientReports() {

        long patientId = userService.getCurrentUser().getId();
        return getPatientReports(patientId);
    }

    public Set<ReportResponse> getPatientReports(long patientId) {
        List<ReportEntity> patientReports = reportRepository.findByPatientId(patientId);

        Set<ReportResponse> response = patientReports.stream()
            .map(this::reportEntityToResponse)
            .collect(Collectors.toSet());
        
        return response;
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

    public record ReportResponse(
        long id,
        ReportStatus reportStatus,
        Date requestAt,
        Date startedAt,
        Date completedAt,
        long doctorId,
        long patientId,
        String reportUrl,
        String reportName,
        LabDetails labDetails
    ) { }

    public record LabDetails(
        long id,
        String name
    ) {
    }
    
}
