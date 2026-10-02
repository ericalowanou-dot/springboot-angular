package com.ucao.dgi.l3.service;

import com.ucao.dgi.l3.dto.AuthDtos.*;
import com.ucao.dgi.l3.entity.Personnel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        refuserRoleLivreur(r.role());
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
        if ((r.role() == User.Role.LIVREUR) != (user.getRole() == User.Role.LIVREUR)) {
            throw new RegleMetierException("Les comptes livreur se gèrent depuis la fiche du livreur (page Personnel)");
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

    // ---------- Accès des livreurs (gérés depuis la page Personnel, par un gérant ou un admin) ----------

    /** Comptes rattachés à une fiche du personnel, indexés par id de fiche. */
    @Transactional(readOnly = true)
    public Map<Integer, UserDto> accesPersonnel() {
        Map<Integer, UserDto> acces = new HashMap<>();
        userRepository.findAllByPersonnelIsNotNull()
                .forEach(u -> acces.put(u.getPersonnel().getIdPersonnel(), UserDto.of(u)));
        return acces;
    }

    public UserDto creerAccesLivreur(Personnel livreur, AccesLivreurRequest r) {
        if (!"LIVREUR".equalsIgnoreCase(livreur.getFonction())) {
            throw new RegleMetierException("Seuls les employés de fonction « Livreur » peuvent recevoir un accès livreur");
        }
        if (userRepository.findByPersonnelIdPersonnel(livreur.getIdPersonnel()).isPresent()) {
            throw new RegleMetierException("Ce livreur a déjà un accès");
        }
        String email = r.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new RegleMetierException("Un compte existe déjà avec cet email");
        }
        if (r.password() == null || r.password().isBlank()) {
            throw new RegleMetierException("Le mot de passe est obligatoire");
        }
        User user = User.builder()
                .email(email)
                .nom(livreur.getNom())
                .prenom(livreur.getPrenom())
                .telephone(livreur.getTelephone())
                .role(User.Role.LIVREUR)
                .personnel(livreur)
                .password(passwordEncoder.encode(r.password()))
                .enabled(r.enabled() == null || r.enabled())
                .build();
        return UserDto.of(userRepository.save(user));
    }

    public UserDto modifierAccesLivreur(Integer idPersonnel, AccesLivreurRequest r) {
        User user = accesDe(idPersonnel);
        String email = r.email().trim().toLowerCase();
        if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new RegleMetierException("Un compte existe déjà avec cet email");
        }
        user.setEmail(email);
        if (r.enabled() != null) {
            user.setEnabled(r.enabled());
        }
        if (r.password() != null && !r.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(r.password()));
        }
        return UserDto.of(user);
    }

    public void supprimerAccesLivreur(Integer idPersonnel) {
        userRepository.delete(accesDe(idPersonnel));
    }

    private User accesDe(Integer idPersonnel) {
        return userRepository.findByPersonnelIdPersonnel(idPersonnel)
                .orElseThrow(() -> new RessourceIntrouvableException("Accès du livreur", idPersonnel));
    }

    private static void refuserRoleLivreur(User.Role role) {
        if (role == User.Role.LIVREUR) {
            throw new RegleMetierException("Les comptes livreur se créent depuis la fiche du livreur (page Personnel)");
        }
    }

    private User trouver(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Utilisateur", id));
    }
}
