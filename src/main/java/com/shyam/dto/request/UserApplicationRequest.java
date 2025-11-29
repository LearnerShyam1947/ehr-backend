package com.shyam.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserApplicationRequest {

    @NotBlank(message = "type is required")
    private String type;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "resumeUrl is required")
    private String resumeUrl;

    @Min(value = 0, message = "experience must be greater than 0")
    private double experience;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    @NotBlank(message = "specialization is required")
    private String specialization;
}
