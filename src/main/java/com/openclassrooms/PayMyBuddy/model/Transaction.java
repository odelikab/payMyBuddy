package com.openclassrooms.PayMyBuddy.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer transactionId;
//	@NotBlank
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "senderId")
	private User sender;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiverId")
//	@JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "userId"))
	private User receiver;
	private String description;
//	@NotBlank
	private int amount;

}
