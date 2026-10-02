package com.ucao.dgi.l3.controller;

import com.ucao.dgi.l3.dto.AuthDtos.AuthResponse;
import com.ucao.dgi.l3.dto.AuthDtos.ChangePasswordRequest;
import com.ucao.dgi.l3.dto.AuthDtos.LoginRequest;
import com.ucao.dgi.l3.dto.AuthDtos.UserDto;
import com.ucao.dgi.l3.entity.User;
import com.ucao.dgi.l3.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest requete) {
        return userService.login(requete);
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal User user) {
        return UserDto.of(user);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changerMotDePasse(@AuthenticationPrincipal User user, @Valid @RequestBody ChangePasswordRequest requete) {
        userService.changerMotDePasse(user, requete);
    }
}
