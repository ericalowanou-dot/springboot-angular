package com.ucao.dgi.l3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Image uploadée, stockée en base plutôt que sur disque :
 * le disque des conteneurs (Render, Docker) est effacé à chaque redéploiement.
 */
@Entity
@Table(name = "image_stockee")
@Getter
@Setter
@NoArgsConstructor
public class ImageStockee {

    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 100, nullable = false)
    private String contentType;

    @Column(nullable = false, length = 10_485_760)
    private byte[] data;

    private LocalDateTime creeLe;
}
