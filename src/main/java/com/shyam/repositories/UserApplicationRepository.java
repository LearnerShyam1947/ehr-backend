package com.shyam.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shyam.entities.UserApplicationEntity;
import java.util.List;

import com.shyam.enums.ApplicationStatus;
import com.shyam.enums.ApplicationType;

@Repository
public interface UserApplicationRepository extends JpaRepository<UserApplicationEntity, Long> {
    Optional<UserApplicationEntity> findByEmail(String email);
    List<UserApplicationEntity> findByType(ApplicationType type);
    List<UserApplicationEntity> findByStatus(ApplicationStatus status);
    Optional<UserApplicationEntity> findByTrackingId(String trackingId);
}
