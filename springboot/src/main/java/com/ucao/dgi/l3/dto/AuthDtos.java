package com.ucao.dgi.l3.dto;

import com.ucao.dgi.l3.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** DTO liés à l'authentification et à la gestion des comptes utilisateurs. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "L'email est obligatoire") String email,
            @NotBlank(message = "Le mot de passe est obligatoire") String password) {
    }

    public record AuthResponse(String token, long expireDans, UserDto utilisateur) {
    }

    public record ChangePasswordRequest(
            @NotBlank String ancienMotDePasse,
            @NotBlank @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
            String nouveauMotDePasse) {
    }

    public record UserRequest(
            @NotBlank(message = "L'email est obligatoire") @Email(message = "Email invalide") String email,
            @NotBlank(message = "Le nom est obligatoire") String nom,
            @NotBlank(message = "Le prénom est obligatoire") String prenom,
            String telephone,
            @NotNull(message = "Le rôle est obligatoire") User.Role role,
            /* Obligatoire à la création, optionnel en modification. */
            @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères") String password,
            Boolean enabled) {
    }

    public record UserDto(Integer id, String email, String nom, String prenom, String telephone,
                          User.Role role, boolean enabled, LocalDateTime derniereConnexion, Integer personnelId) {

        public static UserDto of(User u) {
            return new UserDto(u.getIdUser(), u.getEmail(), u.getNom(), u.getPrenom(), u.getTelephone(),
                    u.getRole(), Boolean.TRUE.equals(u.getEnabled()), u.getDerniereConnexion(),
                    u.getPersonnel() == null ? null : u.getPersonnel().getIdPersonnel());
        }
    }

    /** Création ou modification de l'accès d'un livreur (mot de passe optionnel en modification). */
    public record AccesLivreurRequest(
            @NotBlank(message = "L'email est obligatoire") @Email(message = "Email invalide") String email,
            @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères") String password,
            Boolean enabled) {
    }
}
