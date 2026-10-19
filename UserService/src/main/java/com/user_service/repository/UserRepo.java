package com.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.user_service.model.User;

import jakarta.persistence.LockModeType;

public interface UserRepo extends JpaRepository<User,Long>{
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT u FROM User u WHERE u.id = :userId")
	Optional<User> findByIdForUpdate(@Param("userId") Long userId);
	
	User findByEmail(String email);
	
	boolean existsByEmail(String email);
}
