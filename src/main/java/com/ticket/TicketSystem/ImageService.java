package com.ticket.TicketSystem;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.IOException;

@Service
public class ImageService {

    private final ResourceLoader resourceLoader;
    
    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public InputStream getImageAsStream(String imagePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:static/" + imagePath);
        return resource.getInputStream();
    }
}
