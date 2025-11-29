package com.shyam.dto.response;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shyam.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private long id;
    private String role;
    private String email;
    private String mfaType;
    private String address;

    private String username;
    private String resumeUrl;
    private double experience;
    private String phoneNumber;
    private boolean mfaEnabled;
    private String specialization;

    @SuppressWarnings("null")
    public static UserResponse toUserResponse(UserEntity userEntity) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userEntity, userResponse);
        userResponse.setRole(userEntity.getRole().name());

        if(userEntity.getMfaType() != null)
            userResponse.setMfaType(userEntity.getMfaType().name());

        return userResponse;
    }

}
