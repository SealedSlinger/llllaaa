package com.ooad.home4paws.Service;

import com.ooad.home4paws.Entity.Report;
import com.ooad.home4paws.Repository.ReportRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final String UPLOAD_DIR = "./uploads";

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    public List<String> storePhotos(List<MultipartFile> files) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        List<String> photoPaths = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(file.getInputStream(), filePath);
                photoPaths.add("/uploads/" + uniqueFileName);
            }
        }
        return photoPaths;
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    public Report updateReport(Long id, Report updatedReport) {
        return reportRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedReport.getName());
                    existing.setPhone(updatedReport.getPhone());
                    existing.setDescription(updatedReport.getDescription());
                    existing.setLocation(updatedReport.getLocation());
                    existing.setPhotos(updatedReport.getPhotos()); // Update to setPhotos
                    return reportRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Report not found with id " + id));
    }

    public boolean deleteReport(Long id) {
        try {
            reportRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false; // report not found
        } catch (Exception e) {
            System.err.println("Error deleting report with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate deletion failed due to an unexpected error
        }
    }
}
