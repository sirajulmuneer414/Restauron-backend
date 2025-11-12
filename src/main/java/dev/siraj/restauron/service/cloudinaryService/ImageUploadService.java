package dev.siraj.restauron.service.cloudinaryService;


import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    String imageUploader(MultipartFile file, String folder);

    boolean deleteImageByUrl(String imageUrl);
}
