package com.ipamc.election.views;


import com.ipamc.election.views.components.RegistrationForm;
import com.ipamc.election.views.components.RegistrationFormBinder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Inscription")
@Route(value = "register", layout = MainLayout.class)

public class InscriptionView extends VerticalLayout {

	 public InscriptionView() {
	        RegistrationForm registrationForm = new RegistrationForm();
	        // Center the RegistrationForm
	        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);

	        add(registrationForm);

	        RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(registrationForm);
	        registrationFormBinder.addBindingAndValidation();
	    }
}
