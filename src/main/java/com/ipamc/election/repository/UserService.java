package com.ipamc.election.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Role;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.payload.response.MessageResponse;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    
    @Autowired
	PasswordEncoder encoder;

    public UserService(@Autowired UserRepository repository, @Autowired RoleRepository roleRepository) {
        this.userRepository = repository;
        this.roleRepository = roleRepository;
    }

    public Optional<User> get(Integer id) {
        return userRepository.findById(id);
    }

    public User update(User entity) {
        return userRepository.save(entity);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public int count() {
        return (int) userRepository.count();
    }
    
    public ResponseEntity<?> createUser(User user) {
    	if (userRepository.existsByUsername(user.getUsername())) {
    		return ResponseEntity
				.badRequest()
				.body(new MessageResponse("Error: Username is already taken!"));
    	}
    	if (userRepository.existsByEmail(user.getEmail())) {
		return ResponseEntity
				.badRequest()
				.body(new MessageResponse("Error: Email is already in use!"));
    	}
		// Create new user's account
		user.setMotDePasse(encoder.encode(user.getMotDePasse()));
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByName(EnumRole.ROLE_USER));
		user.setEstCertifie(false);
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
