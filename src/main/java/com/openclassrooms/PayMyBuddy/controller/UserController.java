package com.openclassrooms.PayMyBuddy.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	/**
	 * Create a new user in db
	 * 
	 * @param an object User
	 * @return home page
	 */
	@PostMapping("/")
	public ModelAndView addUser(@Valid User user, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("index");
		}
		userService.addUser(user);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/");
		return new ModelAndView(redirectView);
	}

	/**
	 * Get home page
	 * 
	 * @return home page
	 */
	@GetMapping("/")
	public ModelAndView getRegister() {
		String viewname = "index";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", new User());
		return new ModelAndView(viewname, model);
	}

	/**
	 * Custom login page
	 * 
	 * @return login page
	 */
	@GetMapping("/login")
	public ModelAndView login() {
		String viewname = "login";
		return new ModelAndView(viewname);
	}

	/**
	 * Get Add connection page
	 * 
	 * @param logged user
	 * @return association page
	 */
	@GetMapping("/addConnection")
	public ModelAndView addAssociation(Principal user) {
		String viewname = "addConnection";
		Map<String, Object> model = new HashMap<String, Object>();
		User userAuth = userService.findUserByUsername(user.getName()).get();
		model.put("user", new UserDepositDTO());
		model.put("thisuser", userAuth.getUsername());
		return new ModelAndView(viewname, model);
	}

	/**
	 * Add connection to other user
	 * 
	 * @param username of user to associate
	 * @return addConnection page
	 * @throws Exception
	 */

	@PostMapping("/addConnection")
	public ModelAndView addConnectUser(Principal user, UserDepositDTO userToAssociate) {
		try {
			userService.addAssociation(user.getName(), userToAssociate.getEmail());
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl("/addConnection");
			return new ModelAndView(redirectView);
		} catch (Exception e) {
			ModelAndView mvProfile = addAssociation(user);
			Map<String, Object> newModel = mvProfile.getModel();
			newModel.put("error", e.getMessage());
			return mvProfile;
		}
	}

	/**
	 * Transfer to associate user
	 * 
	 * @param logged user
	 * @return transfer page
	 */
	@GetMapping("/transfer")
	public ModelAndView transferToFriend(Principal user) {
		String viewname = "transfer";
		Map<String, Object> model = new HashMap<String, Object>();
		User userAuth = new User();
		model.put("user", new User());
		model.put("transac", new UserTransferDTO());

		userAuth = userService.findUserByUsername(user.getName()).get();
		Set<String> list = userService.listAssociates(userAuth.getUserId());
		List<Transaction> listTransac = new ArrayList<Transaction>();
		listTransac = userService.findTransactionBySender(user.getName());
		model.put("alltransac", listTransac);
		model.put("list", list);
		model.put("userAuth", userAuth);
		return new ModelAndView(viewname, model);

	}

	/**
	 * Transfer to associate user
	 * 
	 * @param amount and partner DTO object
	 * @param logged user
	 * @return transfer page
	 * @throws Not enough money Exception
	 */
	@PostMapping("/transfer")
	public ModelAndView homePost(UserTransferDTO transactionDTO, Principal user) throws Exception {
		try {
			transactionDTO.setSenderUsername(user.getName());
			userService.transferToFriend(transactionDTO);

			RedirectView redirectView = new RedirectView();
			redirectView.setUrl("/transfer");
			return new ModelAndView(redirectView);
		} catch (Exception e) {
			ModelAndView mvProfile = transferToFriend(user);
			Map<String, Object> newModel = mvProfile.getModel();
			newModel.put("error", e.getMessage());
			return mvProfile;
		}
	}

	/**
	 * Get profile page
	 * 
	 * @param logged user
	 * @return profile page
	 */
	@GetMapping("/profile")
	public ModelAndView profileGet(Principal user) {
		String viewname = "profile";
		Map<String, Object> model = new HashMap<String, Object>();
		User userAuth = userService.findUserByUsername(user.getName()).get();
		model.put("user", new UserDepositDTO());
		model.put("thisuser", userAuth);
		return new ModelAndView(viewname, model);
	}

	/**
	 * Deposit from bank account
	 * 
	 * @param DepositDTO object
	 * @return profile page
	 */
	@PostMapping("/profile")
	public ModelAndView profilePost(Principal user, UserDepositDTO userDepositDTO) {
		userDepositDTO.setEmail(userService.findUserByEmail(user.getName()).get().getEmail());
		userService.deposit(userDepositDTO);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/profile");
		return new ModelAndView(redirectView);
	}

	/**
	 * Transfer to Bank account
	 * 
	 * @param DTO object
	 * @return profile page
	 * @throws Exception
	 */
	@PostMapping("/withdraw")
	public ModelAndView transferToBankAccount(UserDepositDTO userWithdraw, Principal user) throws Exception {
		userWithdraw.setEmail(userService.findUserByEmail(user.getName()).get().getEmail());
		try {
			userService.transferToBank(userWithdraw);
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl("/profile");
			return new ModelAndView(redirectView);
		} catch (Exception e) {
			e.printStackTrace();
			ModelAndView mvProfile = profileGet(user);
			Map<String, Object> newModel = mvProfile.getModel();
			newModel.put("error", e.getMessage());
			return mvProfile;
		}
	}

//	@DeleteMapping("/user/{id}")
//	public String deleteUser(@PathVariable("id") final Integer id) {
//		userService.deleteUser(id);
//		return "user " + id + " deleted";
//	}

//	@PutMapping("/user/{id}")
//	public String updateUser(@PathVariable Integer id, @RequestBody User user) {
//		userService.findUserById(id);
//		return "user " + id + " deleted";
//	}

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

//	@GetMapping("/{username}")
//	public User getUserByUsername(@PathVariable String username) {
//		Optional<User> user = userService.findUserByUsername(username);
//		if (user.isPresent()) {
//			return user.get();
//		} else {
//			return null;
//		}
//	}

}
