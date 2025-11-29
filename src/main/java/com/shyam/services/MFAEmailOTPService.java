package com.shyam.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.shyam.entities.UserEntity;
import com.shyam.enums.MFAType;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.exceptions.TokenExpiredException;
import com.shyam.exceptions.InvalidOTPException;
import com.shyam.repositories.UserRepository;
import com.shyam.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MFAEmailOTPService {
    
    private final EmailService emailService;
    private final UserRepository userRepository;

    private UserEntity getUserByEmail(String email) throws RequestedEntityNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) 
            throw new RequestedEntityNotFoundException("Unable to find user with email " + email);

        return user;
    }
    
    public UserEntity verifyOTPEmailOTP(
        String email,
        String otp
    ) 
    throws RequestedEntityNotFoundException, 
            InvalidOTPException, 
            TokenExpiredException {

        UserEntity user = getUserByEmail(email);
        
        if (user.getOtp() == null || !otp.equals(user.getOtp())) 
            throw new InvalidOTPException();

        if (user.getExpireTime() == null || user.getExpireTime().before(new Date())) 
            throw new TokenExpiredException();
        
        user.setOtp(null);
        user.setMfaType(MFAType.EMAIL);
        user.setMfaEnabled(true);
        user.setExpireTime(null);
        return userRepository.save(user);
                
    }

    public void sendEmailOTP(String email) throws RequestedEntityNotFoundException {
        Date expiration = Utils.getAddedDate(1, Calendar.HOUR);

        UserEntity user = getUserByEmail(email);
        user.setOtp(Utils.generateOTP());
        user.setExpireTime(expiration);
        UserEntity userWithOTP = userRepository.save(user);

        
        log.warn("Sending otp through email : {}", email);
        emailService.send2FAOtpEmail(userWithOTP);
    }

}