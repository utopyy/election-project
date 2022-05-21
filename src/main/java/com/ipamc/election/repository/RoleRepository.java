package com.ipamc.election.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
		Role findByName(EnumRole name);
		Role findById(int id);
}
