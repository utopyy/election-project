package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.ResultatsJury;

public interface ResultatsJuryRepository extends JpaRepository<ResultatsJury,Integer>{
	
	ResultatsJury findFirstByOrderByDateDesc();
}
