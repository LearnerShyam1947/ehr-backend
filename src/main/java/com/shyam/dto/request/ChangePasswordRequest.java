package com.shyam.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    
    @NotBlank(message = "oldPassword is required.")
    private String oldPassword;
    
    @NotBlank(message = "newPassword is required.")
    private String newPassword;
    
    @NotBlank(message = "retypePassword is required.")
    private String retypePassword;
    
}
