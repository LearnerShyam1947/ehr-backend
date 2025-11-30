package com.shyam.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.dto.request.HospitalRequest;
import com.shyam.dto.response.HospitalResponse;
import com.shyam.entities.HospitalEntity;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.services.HospitalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hospitals")
public class HospitalController {
    
    private final HospitalService hospitalService;

    @GetMapping
    public ResponseEntity<List<HospitalResponse>> getHospitals() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }
   
    @GetMapping("/{hospital-id}")
    public ResponseEntity<HospitalEntity> getHospitalById(
        @PathVariable("hospital-id") long id
    ) throws RequestedEntityNotFoundException {
        return ResponseEntity.ok(hospitalService.getHospitalById(id));
    }

    @PostMapping
    public ResponseEntity<HospitalEntity> createHospital(
        @RequestBody HospitalRequest hospitalRequest
    ) throws EntityAlreadyExistsException {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(hospitalService.saveHospital(hospitalRequest));
    }

    @PutMapping("/{hospital-id}")
    public ResponseEntity<Map<String, Object>> updateHospitalById(
        @PathVariable("hospital-id") long id,
        @RequestBody HospitalRequest hospitalRequest
    ) throws RequestedEntityNotFoundException {
        
        HospitalEntity updatedHospital = hospitalService.updateHospitalById(id, hospitalRequest);

        return ResponseEntity
                .ok()
                .body(Map.of(
                    "message", "Hospital with id " + id + " updated successfully",
                    "hospital", HospitalResponse.toResponse(updatedHospital)
                ));
    }

    @DeleteMapping("/{hospital-id}")
    public ResponseEntity<Map<String, Object>> deleteHospitalById(
        @PathVariable("hospital-id") long id
    ) throws RequestedEntityNotFoundException {
        HospitalEntity deletedHospital = hospitalService.deleteHospitalById(id);    

        return ResponseEntity
                .ok()
                .body(Map.of(
                    "message", "Hospital with id " + id + " deleted successfully",
                    "hospital", deletedHospital
                ));
    }
    
}

