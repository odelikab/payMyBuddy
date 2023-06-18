package com.openclassrooms.PayMyBuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.PayMyBuddy.model.Transaction;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	List<Transaction> findBySenderUsername(String senderUsername);
}
