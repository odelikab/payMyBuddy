package com.openclassrooms.PayMyBuddy.controller;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.service.UserService;

import lombok.Data;

@Data
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/saveuser")
	public User addUser(@RequestParam User user) {
		return userService.addUser(user);
	}

	@GetMapping("/login")
	public Iterable<User> getUsers() {
		return userService.getUsers();
//		return "yes";
	}

	@RolesAllowed("USER")
	@GetMapping("/*")
	public String getUser() {
		return "Welcome User";
	}

	@RolesAllowed({ "USER", "ADMIN" })
	@GetMapping("/admin")
	public String getAdmin() {
		return "Welcome Admin";
	}

}
