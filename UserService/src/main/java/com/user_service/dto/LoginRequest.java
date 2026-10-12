package com.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
	@NotBlank(message = "El email es obligatorio")
	@Email
	String email,
	@NotBlank(message = "La contraseña es obligatoria")
	String password) 
{}
