package com.openclassrooms.PayMyBuddy.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTransferDTO {

	private String senderUsername;
	private String receiverUsername;
	private int transferAmount;
	private String description;
}
