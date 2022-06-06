package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.data.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote,Integer> {

	Vote findByUser(User user);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update Votes_categories set Votes_categories.reponse = :reponse where Votes_categories.id_vote = :id_vote AND Votes_categories.id_categorie = :id_categorie", nativeQuery = true)
	int updateAnswerCategorie(@Param("reponse") String reponse, @Param("id_vote") Long id_vote, @Param("id_categorie") Long id_categorie);
}
