package com.jewelryshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
    private String imageUrl;
    private String imageName;
    private String imagePath;
    private Long fileSize;
    private String mimeType;
    private Boolean isPrimary;
}
