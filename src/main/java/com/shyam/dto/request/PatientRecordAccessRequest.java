package com.shyam.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientRecordAccessRequest {
    
    private long userId;

    private String expire;

}
