package com.ipamc.election.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipamc.election.repository.UserService;
import com.ipamc.election.views.components.RegisterForm;
import com.ipamc.election.views.components.RegisterFormBinding;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Inscription")
@Route(value = "register", layout = MainLayout.class)

public class InscriptionView extends VerticalLayout {
	

	private UserService userService;
	
	public InscriptionView(UserService userService, RegisterForm form) {
		RegisterForm registerForm = new RegisterForm();
		this.userService = userService;
	    // Center the RegistrationForm
	    setHorizontalComponentAlignment(Alignment.CENTER, registerForm);
	    add(registerForm);
	    RegisterFormBinding registrationFormBinder = new RegisterFormBinding(registerForm, userService);
	    registrationFormBinder.addBindingAndValidation();
	}
}
	 

