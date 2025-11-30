package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRequest {
    
    @NotBlank(message = "name is required")
    private String name;
    
    @NotBlank(message = "city is required")
    private String city;
    
    @NotBlank(message = "state is required")
    private String state;
    
    @NotBlank(message = "email is required")
    private String email;
    
    @NotBlank(message = "address is required")
    private String address;
    
    @NotBlank(message = "Zip Code is required")
    private String zipCode;
    
    @NotBlank(message = "country is required")
    private String country;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @NotBlank(message = "Phone Number is required")
    private String phoneNumber; 
    
    private String website;
    private int totalBeds;
    private int availableBeds;
    private int establishedYear;
    private boolean emergencyServices;
    private boolean ambulanceServices;

}
