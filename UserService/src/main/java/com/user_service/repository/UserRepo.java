package com.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.model.User;

public interface UserRepo extends JpaRepository<User,Long>{
	Optional<User> findByEmail(String email);
	
	boolean existsByEmail(String email);
}
