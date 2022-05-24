
package com.ipamc.election.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Role;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.error.UserAlreadyExistException;
import com.ipamc.election.payload.request.SignupRequest;
import com.ipamc.election.repository.RoleRepository;
import com.ipamc.election.repository.UserRepository;

@Service
@Transactional 
public class UserService implements IUserService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private RoleRepository roleRepository;
    
    @Autowired
	PasswordEncoder encoder;

    public UserService() {
        
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
    
    @Override
    public User registerNewUserAccount(SignupRequest signupRequest) throws UserAlreadyExistException {
        if(usernameExist(signupRequest.getUsername())) {
        	throw new UserAlreadyExistException(signupRequest.getUsername()+" est déjà pris!");
        }
    	if (emailExist(signupRequest.getEmail())) {
            throw new UserAlreadyExistException("Il existe déjà un compte enregistré avec cette adresse: "
              + signupRequest.getEmail());
        }
        final User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(EnumRole.ROLE_USER));
        user.setRoles(roles); 
        System.out.println("http://localhost:8090/activate?code="+ user.getActivationCode());
        return userRepository.save(user);
    }
    
    public void activate(String activationCode) throws Exception {
    	User user = userRepository.getByActivationCode(activationCode);
    	if(user != null) {
    		user.setActive(true);
    		userRepository.save(user);
    	} else {
    		throw new Exception();
    	}
    	
    }
    

    
    public boolean emailExist(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean usernameExist(String username) {
    	return userRepository.existsByUsername(username);
    }
    
    
}
