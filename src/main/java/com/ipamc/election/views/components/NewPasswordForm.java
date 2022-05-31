package com.ipamc.election.views.components;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class NewPasswordForm extends FormLayout {

	   private H3 title;

	   private PasswordField password;
	   private PasswordField passwordConfirm;
	   private Span errorMessageField;

	   private Button confirm;
	   
	   private UserService tools;
	   
	   private Boolean enablePasswordValidation;
	   
	   private String token;
	   
	   public NewPasswordForm() {
		   
	   }

	   public NewPasswordForm(UserService tools) {
		   this.tools = tools;
	       title = new H3("Nouveau mot de passe");
	       password = new PasswordField("Mot de passe");
	       passwordConfirm = new PasswordField("Confirmation");
	       errorMessageField = new Span();
	       enablePasswordValidation = false;

	       setRequiredIndicatorVisible(password, passwordConfirm);
	       
	       confirm = new Button("Réinitialiser");
	       confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	       confirm.setEnabled(false);
	       Button login = new Button("Se connecter");
    	   login.addClickListener(event ->{
    		   UI.getCurrent().navigate("/login");
    	   });
	       
	       confirm.addClickListener(event ->{
	    	   tools.processResetPassword(token, password.getValue());
	    	   removeAll();
	    	   title.setText("Le mot de passe a été réinitialisé avec succès!");
	    	   add(title, login);  
	       });
	       Hr hr = new Hr();

	       add(title, password, passwordConfirm, errorMessageField, hr, new Label(), confirm);

	       setMaxWidth("504px");
	       setResponsiveSteps(
	               	new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
	               	new ResponsiveStep("168px", 2, ResponsiveStep.LabelsPosition.TOP),
	       			new ResponsiveStep("336px", 3, ResponsiveStep.LabelsPosition.TOP));

	       setColspan(title, 3);
	       setColspan(password, 3);
	       setColspan(passwordConfirm, 3);
	       setColspan(errorMessageField, 3);
	       setColspan(hr, 3);
	       setColspan(confirm,1);
	       setColspan(login, 3);
	       
	       BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
		    binder.bindInstanceFields(this);
		       binder.forField(password)
               .withValidator(this::passwordValidator).bind("password");
		       
		    passwordConfirm.addValueChangeListener(e -> {
		           // The user has modified the second field, now we can validate and show errors.
		           // See passwordValidator() for how this flag is used.
		           enablePasswordValidation = true;
		           binder.validate();
		       });

		       // Set the label where bean-level error messages go
		    binder.setStatusLabel(errorMessageField);
	    }
	   

	   
	   public PasswordField getPasswordField() { return password; }

	   public PasswordField getPasswordConfirmField() { return passwordConfirm; }


	   private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
	       Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	   }
	   
	   private ValidationResult passwordValidator(String pass1, ValueContext ctx) {
	       /*
	        * Just a simple length check. A real version should check for password
	        * complexity as well!
	        */

	       if (pass1 == null || pass1.length() < 8) {
	    	   confirm.setEnabled(false);
	           return ValidationResult.error("Le mot de passe doit faire minimum 8 caractères.");
	       }

	       if (!enablePasswordValidation) {
	           // user hasn't visited the field yet, so don't validate just yet, but next time.
	           enablePasswordValidation = true;
	           return ValidationResult.ok();
	       }

	       String pass2 = passwordConfirm.getValue();

	       if (pass1 != null && pass1.equals(pass2)) {
	    	   confirm.setEnabled(true);
	           return ValidationResult.ok();
	       }
	       confirm.setEnabled(false);
	       return ValidationResult.error("Les mots de passe ne correspondent pas.");
	   }

	public void setTitle(H3 title) {
		this.title = title;
	}

	public void setPassword(PasswordField password) {
		this.password = password;
	}

	public void setPasswordConfirm(PasswordField passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public void setErrorMessageField(Span errorMessageField) {
		this.errorMessageField = errorMessageField;
	}

	public void setConfirm(Button confirm) {
		this.confirm = confirm;
	}

	public void setTools(UserService tools) {
		this.tools = tools;
	}

	public void setEnablePasswordValidation(Boolean enablePasswordValidation) {
		this.enablePasswordValidation = enablePasswordValidation;
	}

	public void setToken(String token) {
		this.token = token;
	}
	   
	   
	   
	   
	   
	   
	   
}