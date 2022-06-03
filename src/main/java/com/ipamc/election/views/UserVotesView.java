package com.ipamc.election.views;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "jury", layout = MainLayout.class)
@PageTitle("Salon de votes")


public class UserVotesView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private TextField pickPseudo;
	
    public UserVotesView(UserService userService, SecurityUtils tools) {
    	this.userService = userService;
    	this.tools = tools;
  	    H4 info = new H4("Indiquez un pseudo pour rejoindre le salon");

  	    pickPseudo = new TextField();
  	    pickPseudo.setMinLength(3);
  	    pickPseudo.setMaxLength(12);
  	    pickPseudo.setHelperText("De 3 à 12 caractères");

  	    Button submitButton = new Button("Rejoindre");
  	    submitButton.setEnabled(false);
  	    submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
  	    
  		pickPseudo.setValueChangeMode(ValueChangeMode.EAGER);
  	    pickPseudo.addValueChangeListener(event ->{
  	    	if(event.getValue().length() > 2 && event.getValue().length() < 13) {
  	    		if(userService.pseudoExists(event.getValue()) && 
  	    				!(userService.getByPseudo(event.getValue()).getUsername().equals(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).getUsername()))) {
  	    			pickPseudo.setInvalid(true);
  	    			pickPseudo.setErrorMessage("Ce pseudo est déjà pris...");
  	    			submitButton.setEnabled(false);
  	    		}else {
  	    			submitButton.setEnabled(true);
  	    		}
  	    	}else {
  	    		pickPseudo.setErrorMessage("");
  	    		submitButton.setEnabled(false);
  	    	}
  	    });
  	    
  	    submitButton.addClickListener(event -> {
  	    	userService.updatePseudo(tools.getAuthenticatedUser().getUsername(),pickPseudo.getValue());
  	    });
  	    
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.setPadding(true);
    	layout.add(pickPseudo);
    	layout.add(submitButton);

  	    add(info, layout);
  	    
	  	setSizeFull();
	    setJustifyContentMode(JustifyContentMode.CENTER);
	    setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	    getStyle().set("text-align", "center");

    }

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
   		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
   			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
   		}

	}
}
