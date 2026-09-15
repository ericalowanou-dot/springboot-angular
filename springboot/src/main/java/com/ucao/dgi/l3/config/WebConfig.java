package com.ucao.dgi.l3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les images depuis le dossier uploads
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().toString().replace("\\", "/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }

}


