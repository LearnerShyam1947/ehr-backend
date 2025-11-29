package com.shyam.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.shyam.dto.request.AppointmentRequest;
import com.shyam.entities.AppointmentEntity;
import com.shyam.enums.AppointmentStatus;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.repositories.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;

    public AppointmentEntity addAppointment(AppointmentRequest request) throws EntityAlreadyExistsException {

        if (appointmentRepository.existsByDoctorIdAndDateAndSlot(request.getDoctorId(), request.getSlot(), request.getDate())) 
            throw new EntityAlreadyExistsException("Doctor is not free in that slot");
        
        if (appointmentRepository.existsByPatientIdAndDateAndSlot(request.getPatientId(), request.getSlot(), request.getDate())) 
            throw new EntityAlreadyExistsException("you already have an anther appointment in same slot");

        AppointmentEntity appointment = new AppointmentEntity();
        BeanUtils.copyProperties(request, appointment);
        appointment.setStatus(AppointmentStatus.CREATED);

        return appointmentRepository.save(appointment);        
    }

    public AppointmentEntity getAppointment(long id) throws RequestedEntityNotFoundException {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RequestedEntityNotFoundException("Appointment record not found with id : " + id));
    }

    public AppointmentEntity changeStatus(AppointmentEntity entity, AppointmentStatus status) {
        entity.setStatus(status);

        return appointmentRepository.save(entity);
    }

    public AppointmentEntity changeStatus(AppointmentEntity entity, AppointmentStatus status, String reason) {
        entity.setStatus(status);
        entity.setRejectionReason(reason);
        
        return appointmentRepository.save(entity);
    }

    public List<AppointmentEntity> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<AppointmentEntity> getDoctorAppointment(long id) {
        return appointmentRepository.findByDoctorId(id);
    }
    
    public List<AppointmentEntity> getPatientAppointment(long id) {
        return appointmentRepository.findByPatientId(id);
    }


}
