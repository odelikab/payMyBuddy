package com.openclassrooms.PayMyBuddy.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int userId;
	@NotBlank
	private String name;
	@NotBlank
	@Column(unique = true, length = 20)
	private String username;
	@NotBlank
	private String password;
	@NotBlank
	@Column(unique = true, length = 50)
	private String email;
	@NotNull
	private Integer accountBalance;
	@ManyToMany
	List<User> associateUser;
	@OneToMany // (mappedBy = "associateUser", cascade = CascadeType.ALL)
	List<User> associateTo = new ArrayList<>();

	public void addAssociate(User user) {

		associateTo.add(user);
//			comment.setProduct(this);
	}

}
