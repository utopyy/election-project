package com.ipamc.election.views;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.repository.UserRepository;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.RegisterConfirmation;
import com.ipamc.election.views.components.RegisterForm;
import com.ipamc.election.views.components.RegisterFormBinding;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Inscription")
@Route(value = "registration", layout = MainLayout.class)

public class RegistrationView extends VerticalLayout implements BeforeEnterObserver {
	
	private UserService userService;
	private UserRepository userRepository;
	private SecurityUtils tools;

	
	public RegistrationView(UserService userService, UserRepository userRepository, SecurityUtils tools) {
		RegisterForm registerForm = new RegisterForm();
		this.tools = tools;
		this.userRepository = userRepository;
		this.userService = userService;
	    setHorizontalComponentAlignment(Alignment.CENTER, registerForm);
	    add(registerForm);
	    RegisterFormBinding registrationFormBinder = new RegisterFormBinding(registerForm, userService, userRepository);
	    registrationFormBinder.addBindingAndValidation();

	}
	
    @Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    	String role = "";
    	if(tools.getAuthenticatedUser()!=null) {	
	    	role = tools.getAuthenticatedUser().getAuthorities().iterator().next().getAuthority();
	    	if(role.equals(EnumRole.ROLE_USER.toString())) {	
		    	beforeEnterEvent.forwardTo(UserVotesView.class);
		    }else{
		    	beforeEnterEvent.forwardTo(AdminVotesView.class);
		    }
    	}
    }
}
	 

