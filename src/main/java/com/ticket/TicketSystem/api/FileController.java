package com.ticket.TicketSystem.api;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class FileController {

    private final ResourceLoader resourceLoader;
    
    @Value("${media.upload.dir}")
    String uploadDirectory;

    public FileController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/uploads/{file_name:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("file_name") String fileName) throws IOException {
        Path filePath = Paths.get(uploadDirectory).resolve(fileName);
        Resource resource = resourceLoader.getResource("file:" + filePath.toString());

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Or return a custom error response
        }

        // Determine content type
        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        // Default to binary data if content type is unknown
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        HttpHeaders headers = new HttpHeaders();
        if (contentType.equals("image/svg+xml")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);
        } else {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        }
        headers.setContentType(MediaType.parseMediaType(contentType));

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
