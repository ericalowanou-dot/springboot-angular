package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.entity.ImageStockee;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.ImageStockeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/** Stocke les images en base de données et les sert via /api/images/{id}. */
@Service
@Transactional
@RequiredArgsConstructor
public class ImageUploadService {

    public static final String PREFIXE_URL = "/api/images/";
    private static final long TAILLE_MAX = 5L * 1024 * 1024;
    private static final Set<String> TYPES_AUTORISES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final ImageStockeeRepository imageRepository;

    /** Enregistre l'image et retourne son URL relative (ex. /api/images/uuid). */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RegleMetierException("Le fichier est vide");
        }
        if (file.getSize() > TAILLE_MAX) {
            throw new RegleMetierException("Image trop volumineuse (5 Mo maximum)");
        }
        String type = file.getContentType();
        if (type == null || !TYPES_AUTORISES.contains(type)) {
            throw new RegleMetierException("Format non supporté (JPEG, PNG, WebP ou GIF uniquement)");
        }
        ImageStockee image = new ImageStockee();
        image.setId(UUID.randomUUID().toString());
        image.setContentType(type);
        image.setData(file.getBytes());
        image.setCreeLe(LocalDateTime.now());
        imageRepository.save(image);
        return PREFIXE_URL + image.getId();
    }

    @Transactional(readOnly = true)
    public ImageStockee trouver(String id) {
        return imageRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Image", id));
    }

    /** Supprime l'image si l'URL pointe vers une image stockée par l'application ; ignore les URL externes. */
    public void supprimerSiInterne(String imageUrl) {
        if (imageUrl == null) {
            return;
        }
        int index = imageUrl.indexOf(PREFIXE_URL);
        if (index < 0) {
            return;
        }
        String id = imageUrl.substring(index + PREFIXE_URL.length());
        // l'identifiant est un UUID : on ignore toute valeur qui n'en a pas la forme
        if (id.matches("[0-9a-fA-F-]{36}")) {
            imageRepository.deleteById(id);
        }
    }
}
