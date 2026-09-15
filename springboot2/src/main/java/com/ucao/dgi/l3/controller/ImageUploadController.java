package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping("/plat")
    public ResponseEntity<?> uploadPlatImage(@RequestParam("file") MultipartFile file) {
        try {
            // Vérifier que le fichier n'est pas vide
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le fichier est vide"));
            }

            // Vérifier le type de fichier (images seulement)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le fichier doit être une image"));
            }

            // Uploader l'image
            String imageUrl = imageUploadService.uploadImage(file, "plats");

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Image uploadée avec succès");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    @DeleteMapping("/image")
    public ResponseEntity<?> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            imageUploadService.deleteImage(imageUrl);
            return ResponseEntity.ok(Map.of("message", "Image supprimée avec succès"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}



