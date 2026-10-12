package com.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.model.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction,Long>{
	List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
