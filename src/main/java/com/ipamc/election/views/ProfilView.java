package com.ipamc.election.views;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

@Route(value = "profil" , layout = MainLayout.class)
@PageTitle("Profil")

public class ProfilView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private User currentUser;

	public ProfilView(UserService userService, SecurityUtils tools) {
		this.userService = userService;
		this.tools = tools;
		currentUser = userService.getByUsername(tools.getAuthenticatedUser().getUsername());
		initProfil();
	}

	private void initProfil() {
		FormLayout fl = new FormLayout();
		TextField mail = new TextField("Adresse mail");
		mail.setValue(currentUser.getEmail());
		PasswordField oldPassword = new PasswordField("Mot de passe");
		PasswordField newPassword = new PasswordField("Nouveau mot de passe");
		PasswordField confirmPass = new PasswordField("Confirmation");
		Span roleSp = createBadgeRole();
		Span sessionSp = createBadgeSessions();
		Span voteSp = createBadgeVotes();
		HorizontalLayout roleLayout = new HorizontalLayout();
		roleLayout.add(roleSp, sessionSp, voteSp);
		HorizontalLayout btnBar = new HorizontalLayout();
		HorizontalLayout title = new HorizontalLayout();
		title.add(new H3("Mon compte"));
		Button maj = new Button("Mettre à jour");
		maj.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btnBar.add(maj);
		btnBar.add(new Button("Annuler"));
		btnBar.setSizeFull();
		btnBar.getStyle().set("margin-top","10px");
		fl.setMaxWidth("550px");
		fl.add(title, mail, oldPassword, newPassword, confirmPass, btnBar);
	    fl.setResponsiveSteps(
	               	new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
	               	new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP),
	       			new ResponsiveStep("490px", 3, ResponsiveStep.LabelsPosition.TOP));

	    fl.setColspan(title, 3);   
	    fl.setColspan(mail, 3);
	       fl.setColspan(oldPassword, 1);
	       fl.setColspan(newPassword, 1);
	       fl.setColspan(confirmPass, 1);
	       fl.setColspan(btnBar, 3);
	       fl.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
	       fl.getStyle().set("padding-left", "20px");
	       fl.getStyle().set("padding-right", "20px");
	       fl.getStyle().set("padding-bottom", "20px");
	       HorizontalLayout wrapper = new HorizontalLayout();
	       wrapper.add(fl);
	    add(wrapper);
	    VerticalLayout vl = new VerticalLayout();
	    vl.setMaxWidth("590px");
	    vl.setWidthFull();
	    vl.add(roleLayout);
	    vl.setAlignItems(Alignment.CENTER);
	    vl.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
	       vl.getStyle().set("padding-left", "20px");
	       vl.getStyle().set("padding-right", "20px");
	    add(vl);
	    setSizeFull();
	    setAlignItems(Alignment.CENTER);
	    getStyle().set("padding-bottom", "100px");
	    setJustifyContentMode(JustifyContentMode.CENTER);
	}

	private Span createBadgeRole() {
		Span role;
		switch(currentUser.getRoles().iterator().next().getName().getName()) {
		case "Membre" :
			role = new Span("Membre");
			role.getElement().getThemeList().add("badge success");
			break;
		case "Administrateur" : 
			role = new Span("Administrateur");
			role.getElement().getThemeList().add("badge success");
			break;
		case "Super administrateur" : 
			role = new Span("Super administrateur");
			role.getElement().getThemeList().add("badge error");
			break;
		default :
			role = new Span("Membre");
			role.getElement().getThemeList().add("badge success");
			break;
		}
		return role;
	}
	
	private Span createBadgeSessions() {
		Span sp = new Span(Integer.toString(currentUser.getJures().size()) + " sessions rejointes");
		sp.getElement().getThemeList().add("badge");
		return sp;
	}
	
	private Span createBadgeVotes() {
		Span sp = new Span(Integer.toString(currentUser.getNbVotes())+ " votes réalisés");
		sp.getElement().getThemeList().add("badge");
		return sp;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}

	}

}
