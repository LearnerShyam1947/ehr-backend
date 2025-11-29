package com.shyam.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.shyam.dto.request.UserApplicationRequest;
import com.shyam.entities.UserApplicationEntity;
import com.shyam.enums.ApplicationStatus;
import com.shyam.enums.ApplicationType;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.repositories.UserApplicationRepository;
import com.shyam.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserApplicationService {
    
    private final UserApplicationRepository userApplicationRepository;

    public UserApplicationEntity apply(UserApplicationRequest userApplicationRequest) throws EntityAlreadyExistsException {
        Optional<UserApplicationEntity> optional =  userApplicationRepository.findByEmail(userApplicationRequest.getEmail());

        if (optional.isPresent()) 
            throw new EntityAlreadyExistsException("Application already exists with email: " + userApplicationRequest.getEmail() );

        UserApplicationEntity userApplicationEntity = new UserApplicationEntity();

        BeanUtils.copyProperties(userApplicationRequest, userApplicationEntity);
        userApplicationEntity.setStatus(ApplicationStatus.APPLIED);
        userApplicationEntity.setTrackingId(Utils.generateRandomString(10));
        userApplicationEntity.setType(ApplicationType.valueOf(userApplicationRequest.getType()));

        return userApplicationRepository.save(userApplicationEntity);
    }
    
    public UserApplicationEntity updateApplicationStatus(long id, ApplicationStatus applicationStatus) throws RequestedEntityNotFoundException {
        UserApplicationEntity userApplicationEntity = userApplicationRepository.findById(id)
                .orElseThrow(() -> new RequestedEntityNotFoundException("Application not found with id: " + id));

        userApplicationEntity.setStatus(applicationStatus);
        return userApplicationRepository.save(userApplicationEntity);
    }

    public UserApplicationEntity getApplicationByTrackingId(String trackingId) throws RequestedEntityNotFoundException {
        UserApplicationEntity userApplicationEntity = userApplicationRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RequestedEntityNotFoundException("Application not found with tracking id: " + trackingId));

        return userApplicationEntity;
    }

    public UserApplicationEntity getApplicationById(long id) throws RequestedEntityNotFoundException {
        UserApplicationEntity userApplicationEntity = userApplicationRepository.findById(id)
                .orElseThrow(() -> new RequestedEntityNotFoundException("Application not found with id: " + id));

        if (userApplicationEntity.getStatus() != ApplicationStatus.APPLIED)
            updateApplicationStatus(id, ApplicationStatus.REVIEWED);

        return userApplicationEntity;
    }

    public List<UserApplicationEntity> getAllApplications() {
        return userApplicationRepository.findAll();
    }

    public List<UserApplicationEntity> getApplicationsByType(ApplicationType type) {
        return userApplicationRepository.findByType(type);
    }

    public List<UserApplicationEntity> getApplicationsByStatus(ApplicationStatus status) {
        return userApplicationRepository.findByStatus(status);
    }

    public void deleteApplication(long id) {
        userApplicationRepository.deleteById(id);
    }
}
