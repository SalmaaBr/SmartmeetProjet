package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
public class FileStorageService {
    private final Path uploadDir = Paths.get("uploads");

    public FileStorageService() {
        // Create the upload directory if it doesn't exist
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }

    public String storeFile(MultipartFile file, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (type.equals("pdf") && !"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("File must be a PDF");
        }
        if (type.equals("image") && !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Generate a unique file name to avoid conflicts
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);

        // Save the file to the upload directory
        Files.copy(file.getInputStream(), filePath);

        // Return the relative path to store in the database
        return filePath.toString();
    }
}
