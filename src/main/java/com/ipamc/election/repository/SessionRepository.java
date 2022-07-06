package com.ipamc.election.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;

public interface SessionRepository extends JpaRepository<Session,Long> {
	
	Session findByName(String session);
	Session findByIsActive(Boolean isActive);
	List<Session> findAllByArchived(Boolean isArchived);
	
	Boolean existsByIsActive(Boolean isActive);
}
