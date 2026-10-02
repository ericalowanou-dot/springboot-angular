package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.DashboardDto;
import com.ucao.dgi.l3.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard")
    public DashboardDto dashboard() {
        return dashboardService.calculer();
    }

    /** Sonde de santé publique (utilisée par Render pour vérifier que le service répond). */
    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
