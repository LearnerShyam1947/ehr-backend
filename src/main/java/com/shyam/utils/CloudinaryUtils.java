package com.shyam.utils;

public class CloudinaryUtils {
    public static final String DOCTOR_APPLICATION_FOLDER = "doctor-applications";
    public static final String LAB_TECHNICIAN_APPLICATION_FOLDER = "lab-technician-applications";

    public static String getPublicId(String publicUrl) {
        String[] parts = publicUrl.split("/");
        int len = parts.length;
        int index = 7;
        
        StringBuilder publicId = new StringBuilder();
        for (int i = index; i < len-1 ; i++) {
            publicId.append(parts[i]);
            publicId.append("/");
        }
        
        publicId.append(parts[len-1]);
        return publicId.toString();
    }

    // public static void main(String[] args) {
    //     String publicUrl = "https://res.cloudinary.com/ddm2qblsr/image/upload/v1720409448/medicines/jo5333daf6puolqthop3.jpg";
    //     System.out.println(getPublicId(publicUrl));
    // }

}
