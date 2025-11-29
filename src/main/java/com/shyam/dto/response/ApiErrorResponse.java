package com.shyam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.time.ZoneId;

@Data
@Builder
@AllArgsConstructor
public class ApiErrorResponse {
    private Date date;
    private String path;
    private String type;
    private String error;
    private int statusCode;
    private Map<String, Object> errors;

    public ApiErrorResponse() {
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        this.date = Date.from(Instant.now().atZone(zoneId).toInstant());
    }
}
