package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.data.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote,Integer> {

}
