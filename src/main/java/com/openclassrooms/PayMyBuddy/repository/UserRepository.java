package com.openclassrooms.PayMyBuddy.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.openclassrooms.PayMyBuddy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Set<User> findByAssociatesUserId(int id);

	@Query("FROM User WHERE user_id = ?1")
	public User findByidJPQL(int id);
}
