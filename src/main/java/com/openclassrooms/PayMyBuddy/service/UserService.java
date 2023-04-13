package com.openclassrooms.PayMyBuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User addUser(User user) {
		User savedUser = userRepository.save(user);
		return savedUser;

	}

	public Iterable<User> getUsers() {
		return userRepository.findAll();
	}

}
