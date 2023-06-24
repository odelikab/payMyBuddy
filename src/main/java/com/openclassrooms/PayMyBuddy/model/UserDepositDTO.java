package com.openclassrooms.PayMyBuddy.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDepositDTO {

	@Email
	private String email;
	@Positive(message = "amount must be between 1 and 1000")
	@Max(5000)
	private double depositAmount;

}
