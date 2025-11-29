package com.shyam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shyam.entities.ReportEntity;
import java.util.List;


@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findByDoctorId(long doctorId);
    List<ReportEntity> findByPatientId(long patientId);
}
