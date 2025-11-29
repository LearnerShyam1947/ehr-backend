package com.shyam.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:cloudinary.properties")
public class MyCloudinaryConfig {

    private final Environment env;

    @Bean
    Cloudinary getCloudinary() {
        Cloudinary cloudinary = new Cloudinary(Map.of(
            "cloud_name", env.getProperty("cloudinary.cloud_name"),
            "api_key", env.getProperty("cloudinary.api_key"),
            "api_secret", env.getProperty("cloudinary.api_secret"),
            "secure", true
        ));
        
        return cloudinary;
    }
}
