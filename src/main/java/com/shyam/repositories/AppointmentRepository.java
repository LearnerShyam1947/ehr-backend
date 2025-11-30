package com.shyam.repositories;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shyam.entities.AppointmentEntity;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AppointmentEntity a WHERE a.patientId = :patientId AND a.date = :date AND a.slot = :slot")
    boolean existsByPatientIdAndDateAndSlot(
            @Param("patientId") long patientId,
            @Param("slot") String slot,
            @Param("date") Date date);
    
    @Query(value = "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AppointmentEntity a WHERE a.doctorId = :doctorId AND a.date = :date AND a.slot = :slot")
    boolean existsByDoctorIdAndDateAndSlot(
            @Param("doctorId") long doctorId,
            @Param("slot") String slot,
            @Param("date") Date date);
    
    List<AppointmentEntity> findByPatientId(long patientId);
    
    List<AppointmentEntity> findByDoctorId(long doctorId);
    
    @Query(value = """
            SELECT 'appointments', COUNT(*) FROM appointments WHERE status = 'CREATED' AND date > CURRENT_TIMESTAMP AND patient_id = :patient_id
            UNION ALL
            SELECT 'reports', COUNT(*) FROM reports WHERE patient_id = :patient_id AND report_status = 'REQUESTED'
            UNION ALL
            SELECT 'access', COUNT(*) FROM patient_access_records WHERE patient_id = :patient_id
            """, nativeQuery = true)
    public List<TableCount> patientDetails(@Param("patient_id") long patientId);
    
    @Query(value = """
            SELECT 'users' AS table_name, COUNT(*) AS cnt FROM users
            UNION ALL
            SELECT 'users_applications', COUNT(*) FROM user_applications WHERE status = 'APPLIED'
            UNION ALL
            SELECT 'hospitals', COUNT(*) FROM hospitals
            """, nativeQuery = true)
    public List<TableCount> adminDetails();
    
    @Query(value = """
            SELECT 'users' AS table_name, COUNT(*) AS cnt FROM users
            UNION ALL
            SELECT 'appointments', COUNT(*) FROM appointments WHERE status = 'CREATED' AND date > CURRENT_TIMESTAMP AND doctor_id = :doctor_id
            UNION ALL
            SELECT 'hospitals', COUNT(*) FROM hospitals
            """, nativeQuery = true)
    public List<TableCount> doctorDetails(@Param("doctor_id") long doctorId);
    
    @Query(value = """
            SELECT 'users' AS table_name, COUNT(*) AS cnt FROM users
            UNION ALL
            SELECT 'reports', COUNT(*) FROM reports WHERE report_status = 'REQUESTED'
            UNION ALL
            SELECT 'hospitals', COUNT(*) FROM hospitals
            """, nativeQuery = true)
    public List<TableCount> labDetails();
    
    public record TableCount(String tableName, long cnt) {}
}