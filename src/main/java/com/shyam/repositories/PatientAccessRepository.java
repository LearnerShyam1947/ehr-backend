package com.shyam.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shyam.entities.PatientAccessListEntity;

@Repository
public interface PatientAccessRepository extends JpaRepository<PatientAccessListEntity, Long> {
    
    public List<PatientAccessListEntity> findByPatientId(long patientId);

    @Query("SELECT p FROM PatientAccessListEntity p WHERE p.userId = :userId AND p.patientId = :patientId")
    public PatientAccessListEntity findByUserIdAndPatientId(
        @Param("userId") long userId,
        @Param("patientId") long patientId
    );

}
