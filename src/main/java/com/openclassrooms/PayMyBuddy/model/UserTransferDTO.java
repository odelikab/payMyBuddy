package com.openclassrooms.PayMyBuddy.model;

import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTransferDTO {

	private String senderUsername;
	private String receiverUsername;
	@Positive
	private int transferAmount;
	private String description;
}
