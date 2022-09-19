package com.ipamc.election.views;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.ipamc.election.validators.EmailValidator;
import com.ipamc.election.views.components.ProfilForm;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;

@Route(value = "profil" , layout = MainLayout.class)
@PageTitle("Profil")

public class ProfilView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private User currentUser;

	private Span errorMessageField = new Span();

	public ProfilView(UserService userService, SecurityUtils tools) {
		this.userService = userService;
		this.tools = tools;
		currentUser = userService.getByUsername(tools.getAuthenticatedUser().getUsername());
		initProfil();
	}

	private void initProfil() {
		ProfilForm profilForm = new ProfilForm(userService, currentUser);		
		profilForm.getMail().setValue(currentUser.getEmail());
		profilForm.getCancel().addClickListener(event -> {
			profilForm.getMail().setValue(currentUser.getEmail());
			profilForm.getOldPassword().clear();
			profilForm.getNewPassword().clear();
			profilForm.getConfirmPass().clear();
			profilForm.getOldPassword().setInvalid(false);
			profilForm.getNewPassword().setInvalid(false);
			profilForm.getConfirmPass().setInvalid(false);
			profilForm.getErrorMessageField().removeAll();
		});
		profilForm.getMaj().addClickListener(event -> {
				try {
					userService.updateProfil(profilForm.getMail().getValue(), profilForm.getOldPassword().getValue(), profilForm.getNewPassword().getValue(), currentUser);
					profilForm.getOldPassword().clear();
					profilForm.getNewPassword().clear();
					profilForm.getConfirmPass().clear();
					profilForm.getErrorMessageField().removeAll();
					currentUser.setEmail(profilForm.getMail().getValue());
				}catch(Exception ex) {
					Label text = new Label("Le mot de passe n'est pas correct.");
					text.getStyle().set("color", "red");
					text.getStyle().set("font-size", "70%");
					profilForm.getCancel().click();
					profilForm.getErrorMessageField().add(text);
				}
		});
		profilForm.getMaj().setEnabled(false);
		HorizontalLayout wrapper = new HorizontalLayout();
		wrapper.add(profilForm);
		add(wrapper);

		Span roleSp = createBadgeRole();
		Span sessionSp = createBadgeSessions();
		Span voteSp = createBadgeVotes();
		HorizontalLayout roleLayout = new HorizontalLayout();
		roleLayout.add(roleSp, sessionSp, voteSp);
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
