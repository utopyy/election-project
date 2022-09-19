package com.ipamc.election.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipamc.election.data.entity.Role;
import com.ipamc.election.repository.RoleRepository;

@Service
@Transactional

public class RoleService {
	
	@Autowired
	RoleRepository roleRepo;
	
	public List<Role> findAll() {
		return roleRepo.findAll();
	}

}
