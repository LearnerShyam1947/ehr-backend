package com.shyam.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import com.shyam.config.custom.MyUserDetails;
import com.shyam.dto.request.ChangePasswordRequest;
import com.shyam.dto.request.MFALoginRequest;
import com.shyam.dto.request.MFARequest;
import com.shyam.dto.request.SetPasswordRequest;
import com.shyam.dto.request.UserRequest;
import com.shyam.dto.response.MFAResponse;
import com.shyam.entities.PatientRecordEntity;
import com.shyam.entities.UserApplicationEntity;
import com.shyam.entities.UserEntity;
import com.shyam.enums.ApplicationType;
import com.shyam.enums.MFAType;
import com.shyam.enums.Role;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.InvalidOTPException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.exceptions.TokenExpiredException;
import com.shyam.repositories.PatientRecordRepository;
import com.shyam.repositories.UserRepository;
import com.shyam.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MFAEmailOTPService mfaEmailOTPService;
    private final PatientRecordRepository patientRecordRepository;
    private final MFAAuthenticatorAppService mfaAuthenticatorAppService;

    @Value("${DEFAULT_ADMIN_EMAIL}")
    private String defaultAdminEmail;

    @Value("${DEFAULT_ADMIN_PASSWORD}")
    private String defaultAdminPassword;


    public UserEntity getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal);
        MyUserDetails user = (MyUserDetails)principal;
        return user.getUserEntity();
        
    }

    @SuppressWarnings("null")
    public UserEntity addUserFromApplication(UserApplicationEntity entity) {
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(entity, user, "id");
        
        // user.setId(0);
        if(entity.getType().equals(ApplicationType.DOCTOR))
            user.setRole(Role.DOCTOR);
        
        if(entity.getType().equals(ApplicationType.LAB_TECHNICIAN))
            user.setRole(Role.LAB_TECHNICIAN);

        user.setToken(UUID.randomUUID().toString());
        user.setExpireTime(Utils.getAddedDate(1, Calendar.HOUR));
        UserEntity updatedUser = userRepository.save(user);
        
        emailService.sendActivationEmail(updatedUser);

        return updatedUser;
    }

    public UserEntity addPatient(UserRequest request) throws EntityAlreadyExistsException {

        UserEntity byEmail = userRepository.findByEmail(request.getEmail());
        if (byEmail != null) 
            throw new EntityAlreadyExistsException("User already exists with same email : " + request.getEmail());
        

        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(request, user);
        user.setRole(Role.PATIENT);
        user.setMfaType(MFAType.NONE);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        UserEntity save = userRepository.save(user);

        PatientRecordEntity patientRecord = new PatientRecordEntity();
        patientRecord.setPatientId(save.getId());
        patientRecordRepository.save(patientRecord);

        return save;
    }

    public UserEntity setPassword(SetPasswordRequest request) throws RequestedEntityNotFoundException, TokenExpiredException {
        UserEntity user = userRepository.findByToken(request.getToken())
                            .orElseThrow(() -> new RequestedEntityNotFoundException());

        if (user.getExpireTime() == null || user.getExpireTime().before(new Date())) 
            throw new TokenExpiredException("The link is expired. please request again for a new link");

        user.setToken(null);
        user.setExpireTime(null);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        return userRepository.save(user);
    }

    public UserEntity forgotPassword(String email) throws RequestedEntityNotFoundException {
        UserEntity byEmail = userRepository.findByEmail(email);

        if(byEmail == null)
            throw new RequestedEntityNotFoundException("No user found with email: " + email);

        byEmail.setToken(UUID.randomUUID().toString());
        byEmail.setExpireTime(Utils.getAddedDate(1, Calendar.HOUR));
        UserEntity save = userRepository.save(byEmail);
        System.out.println(save);

        emailService.sendForgotPasswordEmail(save);

        return save;
    }

    public UserEntity changePassword(ChangePasswordRequest request, long userId) throws RequestedEntityNotFoundException {
        UserEntity userById = userRepository.findById(userId)
                                .orElseThrow(() -> new RequestedEntityNotFoundException());

        if(!passwordEncoder.matches(request.getOldPassword(), userById.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "old password was incorrect");

        if(!request.getNewPassword().equals(request.getRetypePassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password doesn't match");

        userById.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        UserEntity save = userRepository.save(userById);

        return save;
    }

    public UserEntity getUserByEmail(String email) throws RequestedEntityNotFoundException {
        UserEntity userByEmail = userRepository.findByEmail(email);

        if (userByEmail == null) 
            throw new RequestedEntityNotFoundException("No user found with email : " + email);

        return userByEmail;
    }

    public List<UserEntity> getPatients() {
        return userRepository.findByRole(Role.PATIENT);
    }
    
    public List<UserEntity> getDoctors() {
        return userRepository.findByRole(Role.DOCTOR);
    }
    
    public List<UserEntity> getLabTech() {
        return userRepository.findByRole(Role.LAB_TECHNICIAN);
    }
    
    public List<UserEntity> getAdmins() {
        return userRepository.findByRole(Role.ADMIN);
    }
    
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }
    
    public Optional<UserEntity> getUserById(long id) {
        return userRepository.findById(id);
    }
    
    @SuppressWarnings("null")
    public List<UserEntity> getAll(Iterable<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public MFAResponse registerMFA(MFARequest request) throws RequestedEntityNotFoundException {
        UserEntity user = userRepository.findByEmail(request.getEmail());

        MFAResponse mfaResponse = new MFAResponse();
        mfaResponse.setMfaType(request.getMfaType());
        mfaResponse.setMessage("MFA registration initiated successfully");

        if (user == null) 
            throw new RequestedEntityNotFoundException("User not found with email : " + request.getEmail());
        
        if (MFAType.AUTHENTICATOR_APP.name().equals(request.getMfaType())) {
            Object[] results = mfaAuthenticatorAppService.mfaSetup(request.getEmail());
            mfaResponse.setQrCodeUrl((String) results[0]);
            mfaResponse.setSecret((String) results[1]);
            return mfaResponse;
        }
       
        if (MFAType.EMAIL.name().equals(request.getMfaType())) {
            mfaEmailOTPService.sendEmailOTP(request.getEmail());
            return mfaResponse;
        }

        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported MFA type");
    }

    public UserEntity verifyMFA(MFALoginRequest request) throws RequestedEntityNotFoundException, TokenExpiredException, InvalidOTPException {
        UserEntity user = null;

        if (MFAType.AUTHENTICATOR_APP.name().equals(request.getMfaType())) {
            user = mfaAuthenticatorAppService.isOtpValid(request.getEmail(), request.getOtp());
        }

        else if (MFAType.EMAIL.name().equals(request.getMfaType())) {
            user = mfaEmailOTPService.verifyOTPEmailOTP(request.getEmail(), request.getOtp());      
        }

        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported MFA type");

        return user;

    }

    public UserEntity createAdmin() {

    UserEntity existingAdmin = userRepository.findByEmail(defaultAdminEmail);
        if (existingAdmin != null) {
            return existingAdmin;
        }
    
        UserEntity user = new UserEntity();
        user.setUsername("ADMIN");
        user.setEmail(defaultAdminEmail);
        user.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));
        user.setRole(Role.ADMIN);
    
        return userRepository.save(user);
    }


}
