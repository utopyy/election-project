package com.ipamc.election.views.components;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@CssImport(value = "./text-field-theme.css", themeFor = "vaadin-text-field")
@Component
public class RegisterForm extends FormLayout {

	   private H3 title;

	   private TextField username;
	   private Label userLab;
	   
	   
	   private EmailField email;

	   private PasswordField password;
	   private PasswordField passwordConfirm;
	   
	   private Span errorMessageField;

	   private Button submitButton;


	   public RegisterForm() {
	       title = new H3("Formulaire");
	       username = new TextField("Nom d'utilisateur");	      
	       email = new EmailField("Email");
	       password = new PasswordField("Mot de passe");
	       passwordConfirm = new PasswordField("Confirmation");

	       setRequiredIndicatorVisible(username, email, password,
	               passwordConfirm);

	       errorMessageField = new Span();

	       submitButton = new Button("Rejoins nous maintenant!");
	       submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

	       add(title, username, email, password,
	               passwordConfirm, submitButton);

	       setMaxWidth("500px");

	       setResponsiveSteps(
	               new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
	               new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

	       setColspan(title, 2);
	       setColspan(username, 2);
	       setColspan(email, 2);
	       setColspan(errorMessageField, 2);
	       setColspan(submitButton, 2);
	   }

	   
	   public PasswordField getPasswordField() { return password; }

	   public PasswordField getPasswordConfirmField() { return passwordConfirm; }

	   public EmailField getEmailField() { return email; }
	   
	   public TextField getUsernameField() { return username; }
	   
	   public Span getErrorMessageField() { return errorMessageField; }

	   public Button getSubmitButton() { return submitButton; }
	   
	   public Label getUserLabel() { return userLab; }
	   public void setUserLabel(String text) { userLab.setText(text); }

	   private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
	       Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	   }
}