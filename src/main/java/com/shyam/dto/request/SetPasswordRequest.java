package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetPasswordRequest {

    @NotBlank(message = "token is required")
    private String token;

    @NotBlank(message = "password is required")
    private String password;
    
    private String conformPassword;
}
