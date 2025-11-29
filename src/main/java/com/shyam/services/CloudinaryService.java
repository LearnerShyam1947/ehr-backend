package com.shyam.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.shyam.utils.CloudinaryUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {
    
    private final Cloudinary cloudinary;

    @SuppressWarnings("rawtypes")
    public String uploadToCloud(MultipartFile file, String folderName) {
        try {
            Map upload = cloudinary.uploader().upload(file.getBytes(), Map.of(
                "overwrite", true,
                "folder" , folderName
            ));

            return ((String) upload.get("secure_url"));
        } 
        catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void deleteFromCloud(String[] urls) {
        List<String> ids = Arrays.asList(urls).stream().map(CloudinaryUtils::getPublicId).collect(Collectors.toList());
        try {
            ApiResponse delete = cloudinary.api().deleteResources(ids, null);
            System.out.println(delete);
        } 
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void deleteFromCloud(String url) {
        try {
            ApiResponse delete = cloudinary.api().deleteResources(Arrays.asList(CloudinaryUtils.getPublicId(url)), null);
            System.out.println(delete);
        } 
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
