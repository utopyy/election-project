package com.ipamc.election.security.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Role;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.repository.UserRepository;
import com.ipamc.election.views.AdminLogsView;
import com.ipamc.election.views.AdminRoomSettingsView;
import com.ipamc.election.views.AdminUsersView;
import com.ipamc.election.views.AdminVotesView;

import com.ipamc.election.views.MainLayout;
import com.ipamc.election.views.ProfilView;
import com.ipamc.election.views.UserVotesView;
import com.vaadin.flow.router.RouteConfiguration;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired
    private UserRepository userRepository;
    

    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        List<String> roles = new ArrayList<>();
        roles.add(user.getRoles().iterator().next().getName().toString());
        if(user.isActive()) {
 
        	return new org.springframework.security.core.userdetails.User(
          user.getUsername(), user.getPassword(), enabled, accountNonExpired,
          credentialsNonExpired, accountNonLocked, getAuthorities(roles));
        }else {
        	throw new UsernameNotFoundException("Le compte n'est pas activé: " + username);
        }
    }
    
    private static List<GrantedAuthority> getAuthorities (List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}