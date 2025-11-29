package com.shyam.entities;

import java.util.Date;

import com.shyam.enums.ReportStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
public class ReportEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(value = EnumType.STRING)
    private ReportStatus reportStatus;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date requestAt;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startedAt;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date completedAt;

    private long doctorId;
    private long patientId;
    private String reportUrl;
    private String reportName;


}
