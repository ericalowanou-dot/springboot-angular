package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.AuthDtos.UserDto;
import com.ucao.dgi.l3.dto.AuthDtos.UserRequest;
import com.ucao.dgi.l3.entity.User;
import com.ucao.dgi.l3.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestion des comptes : réservée aux administrateurs (voir SecurityConfig). */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto creer(@Valid @RequestBody UserRequest requete) {
        return userService.creer(requete);
    }

    @PutMapping("/{id}")
    public UserDto modifier(@PathVariable Integer id, @Valid @RequestBody UserRequest requete,
                            @AuthenticationPrincipal User courant) {
        return userService.modifier(id, requete, courant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id, @AuthenticationPrincipal User courant) {
        userService.supprimer(id, courant);
    }
}
