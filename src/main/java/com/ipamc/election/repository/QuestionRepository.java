package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;

public interface QuestionRepository extends JpaRepository<Question,Integer> {

	Question findById(Long id);
} 

