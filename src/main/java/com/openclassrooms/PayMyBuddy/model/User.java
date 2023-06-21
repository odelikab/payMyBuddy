package com.openclassrooms.PayMyBuddy.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int userId = -1;
	@NotBlank(message = "this field must not be empty")
	private String name;
	@NotBlank(message = "this field must not be empty")
	@Column(unique = true, length = 50)
	private String username;
	@NotBlank(message = "this field must not be empty")
	private String password;
	@NotBlank(message = "this field must not be empty")
	@Column(unique = true, length = 50)
	private String email;
	private double accountBalance;

	@ManyToMany // FetchType.LAZY by default
	Set<User> associateTo;

	public void addAssociate(User user) {
		associateTo.add(user);
	}

}
