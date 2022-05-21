package com.ipamc.election.views.components;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class RegisterForm extends FormLayout {

	   private H3 title;

	   private TextField username;
	   private EmailField email;

	   private PasswordField password;
	   private PasswordField passwordConfirm;
	   
	   private Span errorMessageField;

	   private Button submitButton;


	   public RegisterForm() {
	       title = new H3("Inscription");
	       username = new TextField("Username");	      
	       email = new EmailField("Email");
	       password = new PasswordField("Password");
	       passwordConfirm = new PasswordField("Confirm password");

	       setRequiredIndicatorVisible(username, email, password,
	               passwordConfirm);

	       errorMessageField = new Span();

	       submitButton = new Button("Join the community");
	       submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

	       add(title, username, email, password,
	               passwordConfirm, submitButton);

	       setMaxWidth("500px");

	       setResponsiveSteps(
	               new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
	               new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

	       setColspan(title, 2);
	       setColspan(email, 2);
	       setColspan(errorMessageField, 2);
	       setColspan(submitButton, 2);
	   }

	   public PasswordField getPasswordField() { return password; }

	   public PasswordField getPasswordConfirmField() { return passwordConfirm; }

	   public Span getErrorMessageField() { return errorMessageField; }

	   public Button getSubmitButton() { return submitButton; }

	   private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
	       Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	   }
}