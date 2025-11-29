package com.shyam.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.dto.request.LoginRequest;
import com.shyam.dto.request.MFALoginRequest;
import com.shyam.dto.request.MFARequest;
import com.shyam.dto.request.SetPasswordRequest;
import com.shyam.dto.request.UserRequest;
import com.shyam.dto.response.MFAResponse;
import com.shyam.dto.response.UserResponse;
import com.shyam.entities.UserEntity;
import com.shyam.enums.MFAType;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.InvalidOTPException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.exceptions.TokenExpiredException;
import com.shyam.services.JwtService;
import com.shyam.services.MFAEmailOTPService;
import com.shyam.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MFAEmailOTPService mfaEmailOTPService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody LoginRequest loginRequest
    ) throws RequestedEntityNotFoundException {
        UserEntity userByEmail = userService.getUserByEmail(loginRequest.getEmail());
        String jwtToken = jwtService.generateJwtToken(userByEmail.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userByEmail.getPasswordHash())) 
            throw new BadCredentialsException("Invalid user details");

        if (userByEmail.getMfaType() != null && MFAType.EMAIL.name().equals(userByEmail.getMfaType().name())) {
            mfaEmailOTPService.sendEmailOTP(userByEmail.getEmail());
        }

        return ResponseEntity.ok(Map.of(
            "statusCode", HttpStatus.OK.value(),
            "user", UserResponse.toUserResponse(userByEmail),
            "token", userByEmail.getMfaType() == null ? jwtToken : ""
        ));
        
    } 
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
        @Valid @RequestBody UserRequest request
    ) throws EntityAlreadyExistsException {
        System.out.println(request);
        UserEntity patient = userService.addPatient(request);
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(Map.of(
                    "user", UserResponse.toUserResponse(patient),
                    "message", "User register successfully, set up MFA"
                ));
    } 

    @PostMapping("/mfa/register")
    public ResponseEntity<MFAResponse> registerMFA(
        @RequestBody MFARequest request
    ) throws RequestedEntityNotFoundException {

        MFAResponse response = userService.registerMFA(request);

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(response);
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<Map<String,Object>> verifyMFA(
        @RequestBody MFALoginRequest request
    ) throws RequestedEntityNotFoundException, TokenExpiredException, InvalidOTPException {
        UserEntity verifyMFA = userService.verifyMFA(request);

        // if (verifyMFA == null) 
        //     return ResponseEntity.badRequest().body(Map.of("error", "invalid credentials"));

        String jwtToken = jwtService.generateJwtToken(verifyMFA.getEmail());

        // todo: create token and include in response
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(Map.of(
                    "message", "MFA verified successfully",
                    "user", UserResponse.toUserResponse(verifyMFA),
                    "token", jwtToken
                ));
    }

    @PostMapping("/set-password")
    public ResponseEntity<Map<String, Object>> setPassword(
        @Valid @RequestBody SetPasswordRequest passwordRequest
    ) throws RequestedEntityNotFoundException, TokenExpiredException {
        UserEntity setPassword = userService.setPassword(passwordRequest);

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(Map.of(
                    "message", "Password was set successfully",
                    "user", setPassword
                ));
    }

    @GetMapping("/admin")
    public UserEntity createAdmin() {
        return userService.createAdmin();
    }
    

}
