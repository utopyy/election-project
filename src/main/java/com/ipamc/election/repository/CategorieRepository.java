package com.ipamc.election.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipamc.election.data.entity.Categorie;

public interface CategorieRepository extends JpaRepository<Categorie,Integer> {

	Categorie findById(Long id);

}

