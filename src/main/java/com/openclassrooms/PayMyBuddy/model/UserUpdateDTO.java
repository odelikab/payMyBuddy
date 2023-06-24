package com.openclassrooms.PayMyBuddy.model;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

	@NotBlank(message = "this field must not be empty")
	private String password;
}
