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

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int userId = -1;
	@NotBlank(message = "ce champ ne doit pas etre vide")
	private String name;
	@NotBlank
	@Column(unique = true, length = 50)
	private String username;
	@NotBlank
	private String password;
	@NotBlank
	@Column(unique = true, length = 50)
	private String email;
	@NotNull
	private double accountBalance;

	@ManyToMany // FetchType.LAZY by default
//	@JoinTable(name = "user_connections", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "associateTo"))
	Set<User> associateTo;

	public void addAssociate(User user) {
		associateTo.add(user);
	}

}
