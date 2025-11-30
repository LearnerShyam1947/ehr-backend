package com.shyam.dto.response;

import org.springframework.beans.BeanUtils;

import com.shyam.entities.HospitalEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponse {
    
    private long id;
    private String name;
    private String city;
    private String state;
    private String email;
    private String status;
    private String address;
    private String zipCode;
    private String country;
    private String website;
    private Integer totalBeds;
    private String phoneNumber; 
    private Integer availableBeds;
    private Integer establishedYear;
    private Boolean emergencyServices;
    private Boolean ambulanceServices;

    @SuppressWarnings("null")
    public static HospitalResponse toResponse(HospitalEntity entity) {

        HospitalResponse response = new HospitalResponse();
        BeanUtils.copyProperties(entity, response);
        response.setStatus(entity.getStatus().name());

        return response;

    }

}
