package com.ipamc.election.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.User;

public interface UserRepository extends JpaRepository<User,Integer> {
	
	User findByUsername(String username);
	User findByEmail(String email);
	User getByActivationCode(String activationCode);
	User findByResetPasswordToken(String token);
	User findByPseudo(String pseudo);
	List<User> findAllByCertified(Boolean certified);
	List<User> findAllByActive(Boolean actived);
	
	Boolean existsByPseudo(String pseudo);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	
	
}
