package com.openclassrooms.PayMyBuddy.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.PayMyBuddy.model.Transaction;
import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.model.UserDepositDTO;
import com.openclassrooms.PayMyBuddy.model.UserTransferDTO;
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

	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void addAssociation(String username, String emailAssociate) throws Exception {
		Optional<User> user = findUserByUsername(username);
		Optional<User> user2 = findUserByEmail(emailAssociate);
		User userAuth = user.get();
		if (user2.isPresent()) {
			User userToAssociate = user2.get();
			if (listAssociates(userAuth.getUserId()).contains(userToAssociate.getUsername())) {
				throw new Exception("User already associated");
			} else {
				userAuth.addAssociate(user2.get());
				userRepository.save(user.get());
			}
		} else {
			throw new Exception("User does not exist in database");
		}

	}

	public User deposit(UserDepositDTO userDepositDTO) {
		Optional<User> user = findUserByEmail(userDepositDTO.getEmail());
		User userFound = user.get();
		userFound.setAccountBalance(userDepositDTO.getDepositAmount() + user.get().getAccountBalance());
		return userRepository.save(user.get());
	}

	public Set<String> listAssociates(int id) {
		User userFound = findUserById(id).get();
		Set<User> list = new HashSet<>();
		Set<String> listUser = new HashSet<>();

		list = userFound.getAssociates();
		for (User user : getUsers()) {
			if (user.getAssociates().contains(userFound))
				listUser.add(user.getUsername());
		}
		for (User user : list) {
			listUser.add(user.getUsername());
		}
		return listUser;
	}

	@Transactional
	public void transferToFriend(UserTransferDTO transactionDTO) throws Exception {
		Optional<User> receiver = findUserByUsername(transactionDTO.getReceiverUsername());
		if (receiver.isEmpty()) {
			throw new Exception("Receiver not found");
		} else {
			User sender = findUserByUsername(transactionDTO.getSenderUsername()).get();
			User receiverFound = receiver.get();
			double transferAmount = transactionDTO.getTransferAmount();
			double fees = transferAmount * 0.5 / 100;
			Transaction transaction = new Transaction();
			transaction.setReceiver(receiverFound);
			transaction.setSender(sender);
			transaction.setAmount(transactionDTO.getTransferAmount());
			transaction.setDescription(transactionDTO.getDescription());
			if (sender.getAccountBalance() - (transferAmount + fees) > 0) {
				sender.setAccountBalance(sender.getAccountBalance() - transferAmount - fees);
				receiverFound.setAccountBalance(transferAmount + receiverFound.getAccountBalance());
				transactionRepository.save(transaction);
			} else
				throw new Exception("Not enough money");

		}
	}

	public List<Transaction> getTransactions() {
		return transactionRepository.findAll();
	}

	public List<Transaction> findTransactionBySender(String sender) {
		return transactionRepository.findBySenderUsername(sender);
	}

	public void transferToBank(UserDepositDTO userDepositDTO) throws Exception {
		Optional<User> user = findUserByEmail(userDepositDTO.getEmail());
		User userFound = user.get();
		if (userFound.getAccountBalance() - userDepositDTO.getDepositAmount() >= 0) {
			userFound.setAccountBalance(userFound.getAccountBalance() - userDepositDTO.getDepositAmount());
			userRepository.save(userFound);
		} else
			throw new Exception("Not enough money");
	}

	public User updateUser(User user) {
		Optional<User> userUpdate = userRepository.findByUsername(user.getUsername());
		User newUser = null;
		if (userUpdate.isPresent()) {
			newUser = userUpdate.get();
			newUser.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(newUser);
		}
		return newUser;
	}
}
