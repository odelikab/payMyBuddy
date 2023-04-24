package com.openclassrooms.PayMyBuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.PayMyBuddy.model.Associate;

@Repository
public interface AssociateRepository extends JpaRepository<Associate, Integer> {

}
