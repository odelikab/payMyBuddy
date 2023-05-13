package com.openclassrooms.PayMyBuddy.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.PayMyBuddy.model.Transaction;
import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.model.UserDepositDTO;
import com.openclassrooms.PayMyBuddy.repository.TransactionRepository;
import com.openclassrooms.PayMyBuddy.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User addUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setAccountBalance(0);
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

	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
		return;
	}

	public User updateUser(User user) {
		Optional<User> userUpdate = userRepository.findByUsername(user.getUsername());
		User newUser = null;
		if (userUpdate.isPresent()) {
			newUser = userUpdate.get();
			newUser.setAccountBalance(user.getAccountBalance());
			newUser.setEmail(user.getEmail());
			userRepository.save(newUser);
		}
		return newUser;
	}

	public void addAssociation(String username, String username2) {
		Optional<User> user = findUserByUsername(username);
		Optional<User> user2 = findUserByUsername(username2);
		if (user2.isPresent()) {
			user.get().addAssociate(user2.get());
			userRepository.save(user.get());
		} else
			return;
	}

	public User deposit(UserDepositDTO userDepositDTO) {
		Optional<User> user = findUserByUsername(userDepositDTO.getUsername());
		User userFound = user.get();
		userFound.setAccountBalance(userDepositDTO.getDepositAmount() + user.get().getAccountBalance());
		return userRepository.save(user.get());
	}

	public Set<String> listAssociates(int id) {
		User userFound = findUserById(id).get();
		Set<User> list = new HashSet<>();
		Set<String> listUser = new HashSet<>();

		list = userFound.getAssociateTo();
		for (User user : getUsers()) {
			if (user.getAssociateTo().contains(userFound))
				listUser.add(user.getUsername());
		}
		for (User user : list) {
			listUser.add(user.getUsername());
		}
		return listUser;
	}

	public void transfer(Transaction transaction) {
		User sender = findUserByUsername(transaction.getSender()).get();
		User receiver = findUserByUsername(transaction.getReceiver()).get();
		if (sender.getAccountBalance() - transaction.getAmount() > 0) {
			sender.setAccountBalance(sender.getAccountBalance() - transaction.getAmount());
			receiver.setAccountBalance(transaction.getAmount() + receiver.getAccountBalance());
			transactionRepository.save(transaction);
			userRepository.save(receiver);
			userRepository.save(sender);
		}

	}

	public List<Transaction> getTransactions() {
		return transactionRepository.findAll();
	}

	public List<Transaction> findTransactionBySender(String sender) {
		return transactionRepository.findBySender(sender);
	}
}
