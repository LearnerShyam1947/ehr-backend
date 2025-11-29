package com.shyam.services;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.shyam.entities.UserEntity;
import com.shyam.repositories.UserRepository;
import com.shyam.utils.Utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final HttpServletRequest request;
    private final Configuration configuration;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Value("${application.frontend.baseUrl}")
    private String frontendBaseUrl;
    
    @Value("${spring.mail.username}")
    private String emailUsername;

    @SuppressWarnings("null")
    public void sendActivationEmail(UserEntity user) {
        System.out.println("Sending activation mail.........");
        
        HashMap<String, Object> map = new HashMap<>();
        Writer out = new StringWriter();

        String link = frontendBaseUrl;
        link = link + "/set-password/"+user.getToken();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(emailUsername, "SecureEHR Admin");
            helper.setTo(user.getEmail());
            helper.setSubject("User activation email | Setup Password");

            Template template = configuration.getTemplate("activate.ftl");

            map.put("userName", user.getUsername());
            map.put("activationLink", link);
            template.process(map, out);

            helper.setText(out.toString(), true);

            javaMailSender.send(mimeMessage);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("null")
    public void send2FAOtpEmail(UserEntity user) {
        System.out.println("Sending 2fa mail.........");
        
        HashMap<String, Object> map = new HashMap<>();
        Writer out = new StringWriter();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(emailUsername, "SecureEHR Admin");
            helper.setTo(user.getEmail());
            helper.setSubject("MFA OTP | Login into account");

            Template template = configuration.getTemplate("2fa-otp.ftl");

            map.put("userName", user.getUsername());
            map.put("otp", user.getOtp());
            template.process(map, out);

            helper.setText(out.toString(), true);

            javaMailSender.send(mimeMessage);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("null")
    public void sendForgotPasswordEmail(UserEntity user) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        user.setToken(UUID.randomUUID().toString());
        user.setExpireTime(Utils.getAddedDate(1, Calendar.HOUR));
        userRepository.save(user);

        try {
            Writer out = new StringWriter();
            HashMap<String, Object> map = new HashMap<>();
            map.put("userName", user.getUsername());

            String link = request.getRequestURL().toString().replace(request.getServletPath(), "");
            link = link + "/auth/validate-token/"+user.getToken();
            map.put("link", link);

            helper.setFrom("secureehr.cloud@gmail.com", "SecureEHR Admin");
            helper.setTo(user.getEmail());
            helper.setSubject("Forgot password email");

            Template template = configuration.getTemplate("forgot-password-email.ftl");
            template.process(map, out);

            helper.setText(out.toString(), true);

            javaMailSender.send(mimeMessage);

        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("null")
    public void sendHtmlMail(String templateUrl, Map<String, Object>  map, String to, String subject) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            Writer out = new StringWriter();

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(emailUsername, "SecureEHR Admin");

            Template template = configuration.getTemplate(templateUrl);
            template.process(map, out);

            helper.setText(out.toString(), true);

            javaMailSender.send(mimeMessage);

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}