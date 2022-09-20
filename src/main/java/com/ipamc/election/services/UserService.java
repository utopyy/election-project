
package com.ipamc.election.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Role;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.error.UserAlreadyExistException;
import com.ipamc.election.payload.request.SignupRequest;
import com.ipamc.election.repository.JureRepository;
import com.ipamc.election.repository.RoleRepository;
import com.ipamc.election.repository.UserRepository;
import com.ipamc.election.repository.VoteRepository;
import com.ipamc.election.validators.EmailValidator;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import ch.qos.logback.classic.Logger;

@Service
@Transactional 
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private JureRepository jureRepository;

	private final MailSender mailSender;

	@Autowired
	PasswordEncoder encoder;

	public UserService(MailSender mailSender) {

		this.mailSender = mailSender;

	}

	public Optional<User> get(Integer id) {
		return userRepository.findById(id);
	}

	public User getByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public User getByEmail(String email) {
		return userRepository.findByEmail(email);
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
		
		sendRegisterMail(user);
		return userRepository.save(user);
	}

	public void updateProfil(String mail, String oldPasswordForm, String newPasswordForm, User currentUser) throws Exception {
		if(!mail.isEmpty()) {
			EmailValidator ev = new EmailValidator();
			if(ev.isValid(mail, null)) {
				currentUser.setEmail(mail);
			}
		}if(!oldPasswordForm.isEmpty()) {
			if(encoder.matches(oldPasswordForm, currentUser.getPassword())) {
				currentUser.setPassword(encoder.encode(newPasswordForm));
			}else {
				throw(new Exception("Le mot de passe est incorrect."));
			}
		}
		userRepository.save(currentUser);
		Notification notification =
				Notification.show("Utilisateur mis à jour!");
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS); 
		notification.setDuration(3000);
	}

	public void sendRegisterMail(User user) {
		String txt = "Bonjour "+user.getUsername()+"!\nVoici le lien pour activer votre compte sur Election: ";
		String code = "https://ras-election.herokuapp.com/activate?code="+ user.getActivationCode();
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setFrom("noreply@example.com");
		message.setSubject("Account confirmation");
		message.setText(txt+code);
		mailSender.send(message);
	}

	public void sendResetMail(String mail, String resetPasswordLink) {
		String txt = "Bonjour "+this.getByEmail(mail).getUsername()+"!\nNous t'envoyons ce mail afin que tu puisses réinitialiser ton mot de passe.\n"
				+ "Si tu n'es pas à l'origine de cette demande, tu n'as rien de plus à faire. Ton mot de passe reste le même.\n\n"
				+ "Pour réinitialiser ton mot de passe:\n"
				+ "1) Clique sur ce lien: "+resetPasswordLink+"\n"
				+ "\nEn cas de problème: tu peux contacter l'administrateur à l'adresse suivante : mlej7498@gmail.com";
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail);
		message.setFrom("Election@Support.gmail");
		message.setSubject("Voici le lien pour réinitialiser votre mot de passe");
		message.setText(txt);
		mailSender.send(message);
	}


	public String processResetPassword(String token, String password) {
		User user = getByResetPasswordToken(token);
		if (user != null) {
			updatePassword(user, password);
			return "OK";
		}
		return "Erreur";
	}


	public void activate(String activationCode) throws Exception {
		Notification notif = new Notification();
		notif.show("trop couuueuette");
		User user = userRepository.getByActivationCode(activationCode);
		if(user != null) {
			user.setActive(true);
			userRepository.save(user);
		} else {
			throw new Exception();
		}

	}

	public void updateResetPasswordToken(String token, String email)  {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.setResetPasswordToken(token);
			userRepository.save(user);
		} 
	}

	public User getByResetPasswordToken(String token) {
		if(token!=null) {
			return userRepository.findByResetPasswordToken(token);
		}
		return null;
	}

	public Boolean pseudoExists(String pseudo) {
		return userRepository.existsByPseudo(pseudo);
	}

	public void updatePseudo(String username, String pseudo) {
		User user = userRepository.findByUsername(username);
		user.setPseudo(pseudo);
		userRepository.save(user);
	}

	public User getByPseudo(String pseudo) {
		return userRepository.findByPseudo(pseudo);
	}
	public boolean emailExist(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean usernameExist(String username) {
		return userRepository.existsByUsername(username);
	}


	public void updatePassword(User user, String newPassword) {
		String encodedPassword = encoder.encode(newPassword);
		user.setPassword(encodedPassword);

		user.setResetPasswordToken(null);
		userRepository.save(user);
	} 

	public Boolean joinsSession(User user, Session session, String pseudo) {
		Boolean pseudoExists = pseudoExists(pseudo);
		if(!pseudoExists || pseudoExists && getByPseudo(pseudo).equals(user)) {
			for(Jure jure : session.getJures()) {
				if(user.equals(jure.getUser())) {
					updatePseudo(user.getUsername(), pseudo);
					jure.setHasJoined(true);
					jureRepository.save(jure);
					break;
				}
			}
			return true;
		}else {
			return false;
		}
	}

	public void leavesSession(Jure jure) {
		jure.setHasJoined(false);
		jureRepository.save(jure);
	}


	public List<User> findAll(){
		return userRepository.findAll();
	}

	public List<User> findCertifiedUsers(){
		return userRepository.findAllByCertified(true);
	}

	public List<User> findAllByCertified(Boolean bool){
		return userRepository.findAllByCertified(bool);
	}

	public List<User> findAllByActive(Boolean bool){
		return userRepository.findAllByActive(bool);
	}

	public List<User> findAllByRole(EnumRole role){
		return userRepository.findAllByRoles_Name(role);
	}

	public void setCertified(User user, Boolean certified) {
		user.setCertified(certified);
		userRepository.save(user);		
	}

	// Currently only one role is allowed, so we clear roles list and simply add the new one 
	public void updateRole(User user, Role role) {
		if(!user.getRoles().contains(role)) {
			user.getRoles().clear();
			user.getRoles().add(role);
			userRepository.save(user);
		}
	}
}
