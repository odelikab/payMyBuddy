package com.openclassrooms.PayMyBuddy.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.service.UserService;

import lombok.Data;

@Data
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@RolesAllowed(value = { "USER" })
	@PostMapping("/register")
	public ModelAndView addUser(User user) {
		userService.addUser(user);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/index");

		return new ModelAndView(redirectView);
	}

	@RolesAllowed({ "USER", "ADMIN" })
	@GetMapping("/register")
	public ModelAndView getAdmin(Model modele) {
		String viewname = "register";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", new User());
//		modele.addAttribute("user", new User());
//		return "register";
		return new ModelAndView(viewname, model);// "Welcome Admin";
	}

	@RolesAllowed(value = { "USER" })
	@GetMapping("/users")
	public Iterable<User> getUsers() {
		return userService.getUsers();
//		return "yes";
	}

	@GetMapping("/user/{id}")
	public User getUserById(@PathVariable int id) {
		Optional<User> user = userService.findUserById(id);
		if (user.isPresent()) {
			return user.get();
		} else {
			return null;
		}
	}

	@GetMapping("/{username}")
	public User getUserByUsername(@PathVariable String username) {
		Optional<User> user = userService.findUserByUsername(username);
		if (user.isPresent()) {
			return user.get();
		} else {
			return null;
		}
	}

	@DeleteMapping("/user/{id}")
	public String deleteUser(@PathVariable int id) {
		userService.deleteUser(id);
		return "user " + id + " deleted";
	}

	@RolesAllowed("USER")
	@GetMapping("/user")
	public Object getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			Object currentUserName = authentication.getName();
			return currentUserName;
		} else
			return "welcome";

//		modele.addAttribute("user", new User());
//		return "welcome";
	}

//	@GetMapping("/addAssociate")
//	public ModelAndView addAssociation(Model modele) {
//		userService.addAssociation();
//	}
}
