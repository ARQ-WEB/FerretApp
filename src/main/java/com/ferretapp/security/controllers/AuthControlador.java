package com.ferretapp.security.controllers;

import com.ferretapp.security.dtos.AuthRequestDTO;
import com.ferretapp.security.dtos.AuthResponseDTO;
import com.ferretapp.security.services.CustomUserDetailsService;
import com.ferretapp.security.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "${ip.frontend}",
        allowCredentials = "true",
        exposedHeaders = "Authorization")
@RestController
@RequestMapping("/api")
public class AuthControlador {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthControlador(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Intento de login fallido para: {}", authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        }

        final UserDetails userDetails =
                userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", token);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setJwt(token);
        response.setRoles(roles);

        log.info("Login exitoso para usuario: {} con roles: {}", authRequest.getUsername(), roles);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(response);
    }
}
