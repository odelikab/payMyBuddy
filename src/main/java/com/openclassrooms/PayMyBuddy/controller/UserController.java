package com.openclassrooms.PayMyBuddy.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.openclassrooms.PayMyBuddy.model.Transaction;
import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.model.UserDepositDTO;
import com.openclassrooms.PayMyBuddy.model.UserTransferDTO;
import com.openclassrooms.PayMyBuddy.service.UserService;

import lombok.Data;

@Data
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ModelAndView addUser(@Valid User user, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("register");
		}
		userService.addUser(user);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/");
		return new ModelAndView(redirectView);
	}

	// @RolesAllowed({ "USER", "ADMIN" })
	@GetMapping("/register")
	public ModelAndView getRegister() {
		String viewname = "register";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", new User());
		return new ModelAndView(viewname, model);
	}

	@GetMapping("/")
	public ModelAndView home(Principal user) {
		String viewname = "index";
		Map<String, Object> model = new HashMap<String, Object>();
		User userAuth = new User();
		model.put("user", new User());
		model.put("transac", new UserTransferDTO());
		model.put("userAuth", userAuth);
		if (user == null) {
			return new ModelAndView(viewname, model);
		} else {
			userAuth = userService.findUserByUsername(user.getName()).get();
			Set<String> list = userService.listAssociates(userAuth.getUserId());
			List<Transaction> listTransac = new ArrayList<Transaction>();
			listTransac = userService.findTransactionBySender(user.getName());// .getTransactions();
			model.put("alltransac", listTransac);
			model.put("list", list);
			model.put("userAuth", userAuth);
			return new ModelAndView(viewname, model);
		}
	}

	@PostMapping("/")
	public ModelAndView homePost(UserTransferDTO transactionDTO, Principal user) {
		Optional<User> receiver = userService.findUserByUsername(transactionDTO.getReceiverUsername());
		if (receiver.isPresent()) {
			Transaction transaction = new Transaction();
			transaction.setReceiver(receiver.get());
			transaction.setSender(userService.findUserByUsername(user.getName()).get());
			transaction.setAmount(transactionDTO.getTransferAmount());
//			transaction.setDescription(null;))
			userService.transfer(transaction);
		}
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/");
		return new ModelAndView(redirectView);
	}

	@GetMapping("/users")
	public Iterable<User> getUsers() {
		return userService.getUsers();
//		return "yes";
	}

//	@GetMapping("/user/{id}")
//	public Object getUserById(@PathVariable int id) {
//		Optional<User> user = userService.findUserById(id);
//		Set<String> list = userService.listAssociates(id);
//		if (user.isPresent()) {
//			return list;
//		} else {
//			return list;
//		}
//	}

	@GetMapping("/{username}")
	public User getUserByUsername(@PathVariable String username) {
		Optional<User> user = userService.findUserByUsername(username);
		if (user.isPresent()) {
			return user.get();
		} else {
			return null;
		}
	}

//	@GetMapping("/user")
//	public Object getUser() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		if (!(authentication instanceof AnonymousAuthenticationToken)) {
//			Object currentUserName = authentication.getName();
//
//			return currentUserName;
//		} else
//			return "welcome";
//		modele.addAttribute("user", new User());
//		return "welcome";
//	}

	@DeleteMapping("/user/{id}")
	public String deleteUser(@PathVariable("id") final Integer id) {
		userService.deleteUser(id);
		return "user " + id + " deleted";
	}

	@PutMapping("/user/{id}")
	public String updateUser(@PathVariable Integer id, @RequestBody User user) {
		userService.findUserById(id);
		return "user " + id + " deleted";
	}

	@GetMapping("/addConnection")
	public ModelAndView addAssociation(Principal user) {
		String viewname = "addConnection";
		Map<String, Object> model = new HashMap<String, Object>();
//		if (user != null) {
		User userAuth = userService.findUserByUsername(user.getName()).get();
//		List<String> list = userService.listAssociates(userAuth.getUserId());
		model.put("user", new UserDepositDTO());
		model.put("thisuser", userAuth.getUsername());
		return new ModelAndView(viewname, model);
//		model.put("list", list);
//		model.put("user", new UserDepositDTO());
	}

	@PostMapping("/addConnection")
	public ModelAndView addConnectUser(Principal user, @Valid UserDepositDTO userA) throws NotFoundException {
//		Optional<User> user4 = userService.findUserByUsername("user4");
		userService.addAssociation(user.getName(), userA.getUsername());
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/");
		return new ModelAndView(redirectView);
	}

	@GetMapping("/profile")
	public ModelAndView profileGet(Principal user) {
		String viewname = "profile";
		Map<String, Object> model = new HashMap<String, Object>();
//		if (user != null) {
		User userAuth = userService.findUserByUsername(user.getName()).get();
//		List<String> list = userService.listAssociates(userAuth.getUserId());
		model.put("user", new UserDepositDTO());
		model.put("thisuser", userAuth.getUsername());
		return new ModelAndView(viewname, model);
//		model.put("list", list);
	}

	@PostMapping("/profile")
	public ModelAndView profilePost(Principal user, UserDepositDTO userA) {
		userA.setUsername(user.getName());
		userService.deposit(userA);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/");
		return new ModelAndView(redirectView);
	}

	@PostMapping("/transfer")
	public ModelAndView transferCompteBancaire(UserDepositDTO userA, Principal user) {
		userA.setUsername(user.getName());
		userService.transferToBank(userA);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/");
		return new ModelAndView(redirectView);
	}
}
