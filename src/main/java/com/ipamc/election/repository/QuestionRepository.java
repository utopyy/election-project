package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;

public interface QuestionRepository extends JpaRepository<Question,Integer> {

	Question findById(Long id);
	Question findByIsActive(Boolean isActive);
	Question findByIntitule(String intitule);
	Question findByIntituleAndSession(String intitule, Session session);
	Boolean existsByIntituleAndSession(String intitule, Session session);
	
	@Modifying
	@Query("DELETE Question WHERE ID = :id")
	void deleteById(Long id);

	@Modifying
	@Query("UPDATE Question SET ISACTIVE = :isActive WHERE ID = :id")
	void activateQuestion(Boolean isActive, Long id);
} 

