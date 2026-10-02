package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.entity.ImageStockee;
import com.ucao.dgi.l3.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    /** Upload d'une image ; renvoie { "imageUrl": "/api/images/<id>" } à enregistrer sur le plat. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return Map.of("imageUrl", imageUploadService.uploadImage(file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> afficher(@PathVariable String id) {
        ImageStockee image = imageUploadService.trouver(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                // une image n'est jamais modifiée (nouvel upload = nouvel id) : cache long
                .cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
                .body(image.getData());
    }
}
