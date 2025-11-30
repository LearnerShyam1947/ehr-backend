package com.shyam.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shyam.entities.HospitalEntity;


@Repository
public interface HospitalRepository extends JpaRepository<HospitalEntity, Long> {
    public Optional<HospitalEntity> findByName(String name);   
}
