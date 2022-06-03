package com.ipamc.election.views;

import java.awt.desktop.UserSessionEvent;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.services.VoteService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "jury", layout = MainLayout.class)
@PageTitle("Salon de votes")


public class UserVotesView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	private VoteService voteService;
	
	private TextField pickPseudo;
	
    public UserVotesView(UserService userService, SecurityUtils tools, SessionService sessionService, VoteService voteService) {
    	this.userService = userService;
    	this.tools = tools;
    	this.sessionService = sessionService;
    	this.voteService = voteService;
    	H2 sessionName = new H2();
    	H4 info = new H4();
    	if(sessionService.checkSessionAccess(userService.getByUsername(tools.getAuthenticatedUser().getUsername()))) {
    		if(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).getHasJoinedSession()) {
    			Session session = sessionService.getActiveSession();
    			for(Question quest : session.getQuestions()) {
    				add(new H3("Question: "+quest.getIntitule()));
    				for(Categorie cat : quest.getCategories()) {
    					if(cat.getLibelle().equals("Note")) {
    						add(new TextField("Mettez une note /"+cat.getValeur()));
    					}else if(cat.getLibelle().equals("Commentaire")) {
    						add(new TextField("Ajoutez un commentaire"));
    					}
    				}
    				if(quest.getPropositions().size()>0) {
	    				CheckboxGroup<Checkbox> checkboxGroup = new CheckboxGroup<>();
	    				checkboxGroup.setLabel("Propositions");
	    				checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
	    				
	    				for(Proposition prop : quest.getPropositions()) {
	        				checkboxGroup.add(new Checkbox(prop.getLibelle()));
	        			}
	    				add(checkboxGroup);
    				}
    			}
    			Button leave = new Button("Quitter le salon");
    			leave.addClickListener(event -> {
    				userService.createVote(userService.getByUsername(tools.getAuthenticatedUser().getUsername()));
    				userService.leavesSession(userService.getByUsername(tools.getAuthenticatedUser().getUsername()));
    				UI.getCurrent().getPage().reload();
    			});
    			
    			add(sessionName, info, leave);
    		}else {
	    		sessionName.setText(sessionService.getActiveSession().getName());
		  	    info.setText("Indiquez un pseudo pour rejoindre le salon");
		  	    pickPseudo = new TextField();
		  	    pickPseudo.setMinLength(3);
		  	    pickPseudo.setMaxLength(12);
		  	    pickPseudo.setHelperText("De 3 à 12 caractères");
		  	    
		  	    Button submitButton = new Button("Rejoindre");
		  	    submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		  	    
		  	    if(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).getPseudo() != null) {
		  	    	pickPseudo.setValue(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).getPseudo());
		  	    	submitButton.setEnabled(true);
		  	    }else {
		  	    	submitButton.setEnabled(false);
		  	    }
	    
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
		  	    	if(!userService.pseudoExists(pickPseudo.getValue()) || (userService.pseudoExists(pickPseudo.getValue()) && userService.getByPseudo(pickPseudo.getValue()).equals(userService.getByUsername(tools.getAuthenticatedUser().getUsername())))) {
		  	    		userService.updatePseudo(tools.getAuthenticatedUser().getUsername(),pickPseudo.getValue()); 
		  	    		userService.joinsSession(userService.getByUsername(tools.getAuthenticatedUser().getUsername()));
		  	    		UI.getCurrent().getPage().reload();
		  	    	}else {
		  	    		pickPseudo.setInvalid(true);
		  	    		pickPseudo.setErrorMessage("Oups, quelqu'un vient juste de prendre ce pseudo...");
		  	    	}
		  	    });
		  	    
		    	HorizontalLayout layout = new HorizontalLayout();
		    	layout.setPadding(true);
		    	layout.add(pickPseudo);
		    	layout.add(submitButton);
		
		  	    add(sessionName, info, layout);
    		}
    	} else {
    		info.setText("Aucun salon de votes n'est disponible pour vous.");
    		add(info);
    	}
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
