package com.openclassrooms.PayMyBuddy.model;

import lombok.Data;

@Data
public class UserTransferDTO {

	private String receiverUsername;
	private int transferAmount;
}
