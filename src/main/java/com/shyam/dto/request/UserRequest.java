package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "username is required")
    private String username;
    
    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;
    
}
