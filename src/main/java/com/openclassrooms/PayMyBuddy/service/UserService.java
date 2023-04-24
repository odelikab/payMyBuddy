package com.openclassrooms.PayMyBuddy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User addUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
//		User newuser = new User(16, "ode", "ode", "pass", "ode", 100, null, null);
//		user.addAssociate(newuser);
		userRepository.save(user);
		return user;
	}

	public Iterable<User> getUsers() {
		return userRepository.findAll();
	}

	public Optional<User> findUserById(int id) {
		return userRepository.findById(id);
	}

	public Optional<User> findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public void deleteUser(int id) {
		userRepository.deleteById(id);
		return;
	}

	public User updateUser(User user) {
		Optional<User> userUpdate = userRepository.findByUsername(user.getUsername());
		User newUser = null;
		if (userUpdate.isPresent())
			newUser = userUpdate.get();
		newUser.setAccountBalance(user.getAccountBalance());
		newUser.setEmail(user.getEmail());
		userRepository.save(newUser);
		return newUser;
	}

	public void addAssociation(String username2) {
		Optional<User> user = findUserByUsername("user4");
		Optional<User> user2 = findUserByUsername(username2);
//		User newuser = new User(16, "ode", "ode", "pass", "ode", 100, null, null);
		user.get().addAssociate(user2.get());
		userRepository.save(user.get());

	}
}
