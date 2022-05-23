package com.ipamc.election.views;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.RegisterForm;
import com.ipamc.election.views.components.RegisterFormBinding;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Inscription")
@Route(value = "register", layout = MainLayout.class)

public class InscriptionView extends VerticalLayout implements BeforeEnterObserver {
	
	private UserService userService;
	private SecurityUtils tools;
	
	public InscriptionView(UserService userService, RegisterForm form, SecurityUtils tools) {
		RegisterForm registerForm = new RegisterForm();
		this.tools = tools;
		this.userService = userService;
	    setHorizontalComponentAlignment(Alignment.CENTER, registerForm);
	    add(registerForm);
	    RegisterFormBinding registrationFormBinder = new RegisterFormBinding(registerForm, userService);
	    registrationFormBinder.addBindingAndValidation();
	}
	
	 @Override
		public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		    if(tools.getAuthenticatedUser()!=null) {
		    	String role = tools.getAuthenticatedUser().getAuthorities().iterator().next().getAuthority();
		    	if(role.equals(EnumRole.ROLE_USER.toString())) {
		    		beforeEnterEvent.forwardTo(VotesView.class);
		    	}else if(role.equals(EnumRole.ROLE_ADMIN.toString()) || role.equals(EnumRole.ROLE_SUPER_ADMIN.toString())) {
		    		beforeEnterEvent.forwardTo(SalonVotesView.class);
		    	}
			}
	    }
}
	 

