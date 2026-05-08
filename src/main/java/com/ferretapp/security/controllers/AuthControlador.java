package com.ferretapp.security.controllers;

import com.ferretapp.security.dtos.AuthRequestDTO;
import com.ferretapp.security.dtos.AuthResponseDTO;
import com.ferretapp.security.services.CustomUserDetailsService;
import com.ferretapp.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

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
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO authRequest) throws Exception {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword())
        );

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

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(response);
    }
}