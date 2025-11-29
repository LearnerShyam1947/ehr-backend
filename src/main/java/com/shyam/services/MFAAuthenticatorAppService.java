package com.shyam.services;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import org.springframework.stereotype.Service;

import com.shyam.entities.UserEntity;
import com.shyam.enums.MFAType;
import com.shyam.exceptions.InvalidOTPException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.repositories.UserRepository;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MFAAuthenticatorAppService {

    private final UserRepository userRepository;

    
    public Object[] mfaSetup(String email) throws RequestedEntityNotFoundException {
        String newSecret = generateNewSecret();
        UserEntity user = getUserByEmail(email);
        user.setSecret(newSecret);
        userRepository.save(user);

        return new Object[] { generateQrCodeImageUri(newSecret, user.getEmail()), newSecret};
    }

    private UserEntity getUserByEmail(String email) throws RequestedEntityNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) 
            throw new RequestedEntityNotFoundException("Unable to find user with email " + email);

        return user;
    }

    
    private String generateNewSecret() {
        return new DefaultSecretGenerator().generate();
    }

    public String generateQrCodeImageUri(String secret) {
        QrData data = new QrData.Builder()
                .label("karnam Shyam")
                .secret(secret)
                .issuer("Springboot EHR TFA")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            e.printStackTrace();
            log.error("Error while generating QR-CODE");
        }

        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

    public String generateQrCodeImageUri(String secret, String email) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer("Springboot EHR TFA")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            e.printStackTrace();
            log.error("Error while generating QR-CODE");
        }

        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

    public UserEntity isOtpValid(String email, String code) throws RequestedEntityNotFoundException, InvalidOTPException {
        UserEntity user = getUserByEmail(email);

        if (!user.isMfaEnabled()) {
            user.setMfaEnabled(true);
            user.setMfaType(MFAType.AUTHENTICATOR_APP);
            userRepository.save(user);
        }

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if(verifier.isValidCode(user.getSecret(), code)) 
            return user;

        else
            throw new InvalidOTPException();
    }
}