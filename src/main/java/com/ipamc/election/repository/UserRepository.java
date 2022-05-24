package com.ipamc.election.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.User;

public interface UserRepository extends JpaRepository<User,Integer> {
	
	User findByUsername(String username);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	User findByEmail(String email);
	User getByActivationCode(String activationCode);
	
	
}
