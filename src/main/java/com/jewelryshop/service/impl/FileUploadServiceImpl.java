package com.jewelryshop.service.impl;

import com.jewelryshop.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            log.info("Starting file upload. Upload directory: {}", uploadDir);
            
            // Validate file
            validateFile(file);

            // Create upload directory if it doesn't exist
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                boolean created = uploadDirectory.mkdirs();
                log.info("Created upload directory: {} (success: {})", uploadDir, created);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID() + "." + fileExtension;

            // Save file
            Path filePath = Paths.get(uploadDir, newFilename).toAbsolutePath();
            Files.write(filePath, file.getBytes());

            log.info("File uploaded successfully: {}", newFilename);

            // Return ONLY the filename (best practice for flexibility)
            return newFilename;

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                return;
            }

            // Extract filename from URL or path
            // Handles both: "filename.jpg" AND "/uploads/filename.jpg"
            String filename = fileUrl;
            if (fileUrl.contains("/")) {
                filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            }

            Path filePath = Paths.get(uploadDir, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", filename);
            } else {
                log.warn("File not found for deletion: {}", filename);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum limit of 5MB");
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        boolean isAllowed = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new RuntimeException("File type not allowed. Only image files are accepted");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
