package com.shyam.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.shyam.dto.request.HospitalRequest;
import com.shyam.dto.response.HospitalResponse;
import com.shyam.entities.HospitalEntity;
import com.shyam.enums.HospitalStatus;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.repositories.HospitalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;

    public List<HospitalResponse> getAllHospitals() {
        List<HospitalResponse> response = hospitalRepository.findAll()
                                            .stream()
                                            .map(HospitalResponse::toResponse)
                                            .collect(Collectors.toList());

        return response;
    }

    public HospitalEntity saveHospital(HospitalRequest request) throws EntityAlreadyExistsException {

        Optional<HospitalEntity> existingHospital = hospitalRepository.findByName(request.getName());

        if (existingHospital.isPresent())
            throw new EntityAlreadyExistsException("Hospital with name " + request.getName() + " already exists.");

        HospitalEntity hospital = new HospitalEntity();
        BeanUtils.copyProperties(request, hospital);
        hospital.setStatus(HospitalStatus.valueOf(request.getStatus()));

        return hospitalRepository.save(hospital);
    }

    public HospitalEntity getHospitalById(long id) throws RequestedEntityNotFoundException {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new RequestedEntityNotFoundException("Hospital with id " + id + " not found"));
    }

    @SuppressWarnings("null")
    public HospitalEntity updateHospitalById(long id, HospitalRequest request) throws RequestedEntityNotFoundException {
        System.out.println("request "+ request);
        HospitalEntity existingHospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RequestedEntityNotFoundException("Hospital with id " + id + " not found"));

        BeanUtils.copyProperties(request, existingHospital);
        existingHospital.setStatus(HospitalStatus.valueOf(request.getStatus()));

        System.out.println(existingHospital);
        HospitalEntity save = hospitalRepository.save(existingHospital);

        return save;
    }

    @SuppressWarnings("null")
    public HospitalEntity deleteHospitalById(long id) throws RequestedEntityNotFoundException {
        HospitalEntity hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RequestedEntityNotFoundException("Hospital with id " + id + " not found"));

        hospitalRepository.delete(hospital);

        return hospital;
    }

}
