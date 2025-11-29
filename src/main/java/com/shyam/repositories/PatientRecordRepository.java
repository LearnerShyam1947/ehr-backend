package com.shyam.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shyam.entities.PatientRecordEntity;


@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecordEntity, Long> {
    Optional<PatientRecordEntity> findByPatientId(long patientId);
}
