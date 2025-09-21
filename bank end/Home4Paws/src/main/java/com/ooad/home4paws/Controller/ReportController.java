package com.ooad.home4paws.Controller;

import com.ooad.home4paws.Entity.Report;
import com.ooad.home4paws.Service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173") // allow Vite frontend
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // CREATE a new report
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Report> createReport(@RequestPart("report") String reportJson,
                                                 @RequestPart(value = "photos", required = false) List<MultipartFile> photos) throws IOException {
        Report report;
        try {
            report = new ObjectMapper().readValue(reportJson, Report.class);
        } catch (IOException e) {
            System.err.println("Error deserializing report JSON: " + reportJson);
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        System.out.println("Received report: " + report.getName() + ", " + report.getDescription());

        if (photos != null && !photos.isEmpty()) {
            List<String> photoPaths = reportService.storePhotos(photos);
            report.setPhotos(photoPaths);
        }

        Report savedReport = reportService.createReport(report);
        return ResponseEntity.ok(savedReport);
    }

    // GET all reports
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    // GET a single report by ID
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE an existing report
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Report> updateReport(@PathVariable Long id,
                                                 @RequestPart("report") String reportJson,
                                                 @RequestPart(value = "photos", required = false) List<MultipartFile> photos) throws IOException {
        Report updatedReport;
        try {
            updatedReport = new ObjectMapper().readValue(reportJson, Report.class);
        } catch (IOException e) {
            System.err.println("Error deserializing update report JSON: " + reportJson);
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        if (photos != null && !photos.isEmpty()) {
            List<String> photoPaths = reportService.storePhotos(photos);
            updatedReport.setPhotos(photoPaths);
        }
        Report report = reportService.updateReport(id, updatedReport);
        return ResponseEntity.ok(report);
    }

    // DELETE a report
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        boolean deleted = reportService.deleteReport(id);
        if (!deleted) {
            return ResponseEntity.notFound().build(); // return 404 instead of 500
        }
        return ResponseEntity.noContent().build(); // 204 on success
    }
}
