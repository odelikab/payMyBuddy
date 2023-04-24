package com.openclassrooms.PayMyBuddy.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "partner")
public class Associate {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int associate_id;

//	private User userA;
//	private User userB;

}
