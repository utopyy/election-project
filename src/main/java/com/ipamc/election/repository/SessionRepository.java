package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;

public interface SessionRepository extends JpaRepository<Session,Long> {
	
	Session findByName(String session);
	Session findByIsActive(Boolean isActive);
	
	Boolean existsByIsActive(Boolean isActive);
}
