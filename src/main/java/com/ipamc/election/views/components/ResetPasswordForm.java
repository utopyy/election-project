package com.ipamc.election.views.components;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.UserService;
import com.ipamc.election.validators.EmailValidator;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ErrorLevel;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@CssImport(value = "./text-field-theme.css", themeFor = "vaadin-text-field")
@Component
public class ResetPasswordForm extends FormLayout {

	   private EmailField email;
	   private EmailValidator validator;
	   private Span errorMessageField;
	   private Paragraph text;
	   private Button submitButton;
	   private UserService userService;


	   public ResetPasswordForm(UserService userService) {
		   
		   this.userService = userService;
		   validator = new EmailValidator();
	       H3 title = new H3("Réinitialiser mon mot de passe");
     
	       email = new EmailField("Email");
	       text = new Paragraph("Entrez l'adresse mail associé à votre compte"
	       		+ " et nous vous enverrons un email avec les instructions pour "
	       		+ "réinitialiser votre mot de passe.");
	       setRequiredIndicatorVisible(email);

	       errorMessageField = new Span();

	       submitButton = new Button("Envoyer les instructions!");
	       submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	       submitButton.setEnabled(false);
	       
	       
	       email.addValueChangeListener(event -> {
	    	   if(validator.isValid(event.getValue(),null)){
	    		   submitButton.setEnabled(true);
	    	   }else {
	    		   submitButton.setEnabled(false);
	    	   }
	       });

	       Button login = new Button("Se connecter");
	       login.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
	       login.setHeight("20px");
	       login.addClickListener(event ->{
	    	   UI.getCurrent().navigate("/login");

	       });

	       Hr h1 =  new Hr();
	       
	       submitButton.addClickListener(event ->{
	    	   if(userService.emailExist(email.getValue())) {
	    		   User user = userService.getByEmail(email.getValue());
	    		   if(user.isActive()) {
	    			   userService.sendResetMail(email.getValue());
	    		   }
	    		   
	    	   }
	    	   removeAll();
	    	   title.setText("Les instructions ont été envoyées!");
	    	   text.setText("Rien reçu? L'adresse indiquée n'est peut être pas liée à un compte enregistré ou ce compte n'a pas encore été activé.");
	    	   add(title, text, h1, new Label(), login, new Label());
	       });
	       
	       add(title, text, email, submitButton, h1, new Label(), login, new Label());

	       setMaxWidth("504px");
	       setResponsiveSteps(
	               	new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
	               	new ResponsiveStep("168px", 2, ResponsiveStep.LabelsPosition.TOP),
	       			new ResponsiveStep("336px", 3, ResponsiveStep.LabelsPosition.TOP));

	       setColspan(title, 3);
	       setColspan(email, 3);
	       setColspan(text, 3);
	       setColspan(errorMessageField, 3);
	       setColspan(submitButton, 3);
	       setColspan(h1,3);
	       setColspan(login,1);
	       
	       BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
	       binder.bindInstanceFields(this);


	       binder.forField(email).withValidator(e -> {
	    	   return validator.isValid(e.toString(), null);
	       }, "L'adresse mail n'est pas valide.", ErrorLevel.ERROR);
	   }
	   

	   public EmailField getEmailField() { return email; }
	   
	   public Span getErrorMessageField() { return errorMessageField; }

	   public Button getSubmitButton() { return submitButton; }
	   
	   private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
	       Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	   }
}
