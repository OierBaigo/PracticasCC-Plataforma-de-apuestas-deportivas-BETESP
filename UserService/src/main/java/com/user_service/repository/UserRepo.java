package com.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.model.User;

public interface UserRepo extends JpaRepository<User,Long>{
	User findByEmail(String email);
	
	boolean existsByEmail(String email);
}
