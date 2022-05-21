package com.ipamc.election.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.repository.UserRepository;
import com.ipamc.election.security.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) {
		try { 
			User user = userRepository.findByUsername(username);
			return UserDetailsImpl.build(user);
		}catch(UsernameNotFoundException e) {
			return null;
		}
	}
}

