package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;

public interface JureRepository extends JpaRepository<Jure, Integer> {

	Jure findBySessionAndUser(Session session, User user);
	Boolean existsBySessionAndUser(Session session, User user);
}
