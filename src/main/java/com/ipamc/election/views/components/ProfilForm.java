package com.ipamc.election.views.components;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.UserService;
import com.ipamc.election.validators.EmailValidator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;


public class ProfilForm extends FormLayout {

	private EmailField email = new EmailField("Adresse mail");
	private PasswordField oldPassword = new PasswordField("Mot de passe");
	private PasswordField newPassword = new PasswordField("Nouveau mot de passe");
	private PasswordField confirmPass = new PasswordField("Confirmation");

	private Span errorMessageField = new Span();

	private Button maj;
	private Button cancel;

	public ProfilForm(UserService userService, User currentUser) {
		HorizontalLayout btnBar = new HorizontalLayout();
		HorizontalLayout title = new HorizontalLayout();
		title.add(new H3("Mon compte"));
		maj = new Button("Mettre à jour");
		maj.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btnBar.add(maj);
		cancel = new Button("Annuler");
		btnBar.add(cancel);
		btnBar.setSizeFull();
		btnBar.getStyle().set("margin-top","10px");
		newPassword.setEnabled(false);
		confirmPass.setEnabled(false);
		setMaxWidth("550px");
		add(title, email, oldPassword, newPassword, confirmPass, errorMessageField, btnBar);
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("490px", 3, ResponsiveStep.LabelsPosition.TOP));

		setColspan(title, 3);   
		setColspan(email, 3);
		setColspan(oldPassword, 1);
		setColspan(newPassword, 1);
		setColspan(confirmPass, 1);
		setColspan(errorMessageField, 3);
		setColspan(btnBar, 3);
		getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		getStyle().set("padding-left", "20px");
		getStyle().set("padding-right", "20px");
		getStyle().set("padding-bottom", "20px");

		initEmailField(userService, currentUser);
		initPasswordField();
	}

	private void initEmailField(UserService userService, User currentUser) {
		email.setValueChangeMode(ValueChangeMode.EAGER);
		email.addValueChangeListener(event -> {
			errorMessageField.removeAll();
			EmailValidator ev = new EmailValidator();
			if(!ev.isValid(event.getValue(), null)) {
				Label text = new Label("L'adresse mail est invalide.");
				text.getStyle().set("color", "red");
				text.getStyle().set("font-size", "70%");
				errorMessageField.add(text);
				email.setInvalid(true);
			}else if(!event.getValue().equals(currentUser.getEmail()) && userService.emailExist(event.getValue())) {
				Label text = new Label("Cette adresse est deja prise.");
				text.getStyle().set("color", "red");
				text.getStyle().set("font-size", "70%");
				errorMessageField.add(text);
				email.setInvalid(true);
			}else {
				email.setInvalid(false);
			}
			btnEnabler();
		});
	}

	// todo
	private void initPasswordField() {
		oldPassword.setValueChangeMode(ValueChangeMode.EAGER);
		oldPassword.addValueChangeListener(event -> {
			errorMessageField.removeAll();
			if(oldPassword.isEmpty()) {
				newPassword.setEnabled(false);
				confirmPass.setEnabled(false);
				newPassword.setInvalid(false);
				confirmPass.setInvalid(false);
				newPassword.clear();
				confirmPass.clear();
			}else {
				if(!newPassword.isEnabled()) {
					newPassword.setEnabled(true);
					confirmPass.setEnabled(true);
				}
			}
			btnEnabler();
		});
		
		newPassword.addValueChangeListener(event -> {
			errorMessageField.removeAll();
			if(newPassword.getValue().isEmpty() || newPassword.getValue().length() < 8) {
				Label text = new Label("Le mot de passe doit faire minimum 8 caractères.");
				text.getStyle().set("color", "red");
				text.getStyle().set("font-size", "70%");
				errorMessageField.add(text);
				newPassword.setInvalid(true);
			}else if(confirmPass.isInvalid() && newPassword.getValue().equals(confirmPass.getValue())) {
				confirmPass.setInvalid(false);
				newPassword.setInvalid(false);
			}else {
				newPassword.setInvalid(false);
			}
			btnEnabler();
		});
		
		confirmPass.setValueChangeMode(ValueChangeMode.EAGER);
		confirmPass.addValueChangeListener(event -> {
			errorMessageField.removeAll();
			if(!confirmPass.getValue().isEmpty() && !newPassword.getValue().equals(confirmPass.getValue())) {
				Label text = new Label("Les mots de passe ne correspondent pas.");
				text.getStyle().set("color", "red");
				text.getStyle().set("font-size", "70%");
				errorMessageField.add(text);
				confirmPass.setInvalid(true);	
				newPassword.setInvalid(true);
			}else {
				confirmPass.setInvalid(false);
				newPassword.setInvalid(false);
			}
			btnEnabler();
		});
	}

	private void btnEnabler() {
		if(email.isInvalid() || oldPassword.isInvalid() || newPassword.isInvalid() || confirmPass.isInvalid()) {
			maj.setEnabled(false);
		}else if(!oldPassword.getValue().isEmpty() && (newPassword.getValue().isEmpty() || confirmPass.getValue().isEmpty())) {
			maj.setEnabled(false);
		}else if(!oldPassword.getValue().isEmpty() && (!newPassword.getValue().equals(confirmPass.getValue()))){
			maj.setEnabled(false);
		}else {
			maj.setEnabled(true);
		}
	}



	public EmailField getMail() {
		return email;
	}

	public PasswordField getOldPassword() {
		return oldPassword;
	}

	public PasswordField getNewPassword() {
		return newPassword;
	}

	public PasswordField getConfirmPass() {
		return confirmPass;
	}

	public Span getErrorMessageField() {
		return errorMessageField;
	}

	public Button getMaj() {
		return maj;
	}

	public Button getCancel() {
		return cancel;
	}



}