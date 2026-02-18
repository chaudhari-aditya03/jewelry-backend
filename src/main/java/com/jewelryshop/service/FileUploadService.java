package com.jewelryshop.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    /**
     * Upload a file and return the file URL
     * @param file the file to upload
     * @return the URL of the uploaded file
     */
    String uploadFile(MultipartFile file);

    /**
     * Delete a file by URL
     * @param fileUrl the URL of the file to delete
     */
    void deleteFile(String fileUrl);
}
