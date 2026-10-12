package com.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank(message = "El email es obligatorio")
		@Email
		String email,
		@NotBlank(message = "El nombre de usuario es obligatorio")
		@Size(min = 3, max = 50,  message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
		String username,
		@NotBlank(message = "La contraseña es obligatoria")
		@Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
		String password) 
{}
