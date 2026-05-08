package com.ferretapp.security.dtos;

import lombok.Data;
import java.util.Set;

@Data
public class AuthResponseDTO {
    private String jwt;
    private Set<String> roles;
}