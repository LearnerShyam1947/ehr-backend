package com.shyam.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.entities.ReportEntity;
import com.shyam.repositories.ReportRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HomeController {

    private final ReportRepository reportRepository;
    
    @GetMapping("/reports") 
    public ResponseEntity<List<ReportEntity>> getReports() {
        List<ReportEntity> all = reportRepository.findAll();

        return ResponseEntity.ok().body(all);
    }


}
