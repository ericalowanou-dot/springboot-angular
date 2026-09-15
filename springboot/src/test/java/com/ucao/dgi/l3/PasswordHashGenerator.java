package com.ucao.dgi.l3;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String gerantPassword = "gerant123";
        
        System.out.println("Hash pour admin123: " + encoder.encode(adminPassword));
        System.out.println("Hash pour gerant123: " + encoder.encode(gerantPassword));
    }
}

