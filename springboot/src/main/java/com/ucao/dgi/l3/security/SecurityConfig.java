package com.ucao.dgi.l3.security;

import com.ucao.dgi.l3.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Règles d'accès :
 * - public : connexion, consultation de la carte (plats, catégories, menus) et des images ;
 * - ADMIN : gestion des comptes utilisateurs ;
 * - ADMIN / GERANT : catalogue, personnel (dont les accès livreur), stocks, fournisseurs, suppressions ;
 * - ADMIN / GERANT / EMPLOYE : commandes, clients, livraisons, tableau de bord ;
 * - LIVREUR : uniquement /api/livreur/** (ses propres livraisons) et son profil.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] GESTION = {"ADMIN", "GERANT"};
    private static final String[] EQUIPE = {"ADMIN", "GERANT", "EMPLOYE"};

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                erreurJson(res, HttpServletResponse.SC_UNAUTHORIZED, "Authentification requise"))
                        .accessDeniedHandler((req, res, e) ->
                                erreurJson(res, HttpServletResponse.SC_FORBIDDEN, "Accès refusé")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/health", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/images/**", "/api/plats/**",
                                "/api/categories/**", "/api/menus/**").permitAll()
                        .requestMatchers("/api/auth/**").authenticated()
                        // le livreur n'accède qu'à son espace, et seul lui y accède
                        .requestMatchers("/api/livreur/**").hasRole("LIVREUR")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // liste du personnel : nécessaire à l'équipe pour assigner les livreurs
                        .requestMatchers(HttpMethod.GET, "/api/personnel").hasAnyRole(EQUIPE)
                        .requestMatchers("/api/personnel/**", "/api/fournisseurs/**", "/api/produits/**",
                                "/api/approvisionnements/**", "/api/images/**", "/api/plats/**",
                                "/api/categories/**", "/api/menus/**").hasAnyRole(GESTION)
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole(GESTION)
                        .requestMatchers("/api/**").hasAnyRole(EQUIPE)
                        // les anciens endpoints (hors /api) ne sont plus exposés
                        .anyRequest().denyAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static void erreurJson(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }
}
