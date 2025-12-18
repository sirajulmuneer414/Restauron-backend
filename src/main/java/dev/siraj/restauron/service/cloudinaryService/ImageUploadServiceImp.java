package dev.siraj.restauron.service.cloudinaryService;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageUploadServiceImp implements ImageUploadService{

    @Resource
    private Cloudinary cloudinary;

    @Override
    public String imageUploader(MultipartFile file, String folder) {
        try {
            Map<Object, Object> options = new HashMap<>();
            options.put("folder", folder);
            options.put("quality", "auto:best");
            options.put("fetch_format", "auto");
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e+" from imageUploadServiceImp");
        }

    }

    @Override
    public boolean deleteImageByUrl(String imageUrl) {
            if(imageUrl == null) return false;
        try {
            // Extract publicId from secure URL (remove version, extension, base)
            String regex = "/upload/(?:v\\d+/)?(.+?)(\\.[\\w]+)?$";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(imageUrl);
            if (matcher.find()) {
                String publicId = matcher.group(1); // e.g. "folder/filename"
                // Now call destroy
                Map result = cloudinary.uploader().destroy(publicId, new HashMap<>());
                return "ok".equals(result.get("result").toString());
            }
            throw new RuntimeException("Could not extract public ID from URL: " + imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
