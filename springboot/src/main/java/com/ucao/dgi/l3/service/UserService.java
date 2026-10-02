package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.AuthDtos.*;
import com.ucao.dgi.l3.entity.User;
import com.ucao.dgi.l3.exception.RegleMetierException;
import com.ucao.dgi.l3.exception.RessourceIntrouvableException;
import com.ucao.dgi.l3.repository.UserRepository;
import com.ucao.dgi.l3.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Authentification et administration des comptes. */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest requete) {
        User user = userRepository.findByEmail(requete.email().trim().toLowerCase())
                .filter(u -> passwordEncoder.matches(requete.password(), u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Ce compte est désactivé");
        }
        user.setDerniereConnexion(LocalDateTime.now());
        return new AuthResponse(jwtService.genererToken(user), jwtService.getExpirationMs(), UserDto.of(user));
    }

    public void changerMotDePasse(User courant, ChangePasswordRequest requete) {
        User user = trouver(courant.getIdUser());
        if (!passwordEncoder.matches(requete.ancienMotDePasse(), user.getPassword())) {
            throw new RegleMetierException("L'ancien mot de passe est incorrect");
        }
        user.setPassword(passwordEncoder.encode(requete.nouveauMotDePasse()));
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll(Sort.by("nom", "prenom")).stream().map(UserDto::of).toList();
    }

    public UserDto creer(UserRequest r) {
        String email = r.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new RegleMetierException("Un compte existe déjà avec cet email");
        }
        if (r.password() == null || r.password().isBlank()) {
            throw new RegleMetierException("Le mot de passe est obligatoire");
        }
        User user = User.builder()
                .email(email)
                .nom(r.nom())
                .prenom(r.prenom())
                .telephone(r.telephone())
                .role(r.role())
                .password(passwordEncoder.encode(r.password()))
                .enabled(r.enabled() == null || r.enabled())
                .build();
        return UserDto.of(userRepository.save(user));
    }

    public UserDto modifier(Integer id, UserRequest r, User courant) {
        User user = trouver(id);
        String email = r.email().trim().toLowerCase();
        if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new RegleMetierException("Un compte existe déjà avec cet email");
        }
        boolean soiMeme = user.getIdUser().equals(courant.getIdUser());
        if (soiMeme && (r.role() != User.Role.ADMIN || Boolean.FALSE.equals(r.enabled()))) {
            throw new RegleMetierException("Vous ne pouvez pas retirer vos propres droits d'administrateur");
        }
        user.setEmail(email);
        user.setNom(r.nom());
        user.setPrenom(r.prenom());
        user.setTelephone(r.telephone());
        user.setRole(r.role());
        if (r.enabled() != null) {
            user.setEnabled(r.enabled());
        }
        if (r.password() != null && !r.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(r.password()));
        }
        return UserDto.of(user);
    }

    public void supprimer(Integer id, User courant) {
        if (id.equals(courant.getIdUser())) {
            throw new RegleMetierException("Vous ne pouvez pas supprimer votre propre compte");
        }
        userRepository.delete(trouver(id));
    }

    private User trouver(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Utilisateur", id));
    }
}
