package com.ipamc.election.views;

import java.awt.desktop.UserSessionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.BroadcastMessageType;
import com.ipamc.election.data.entity.BroadcastMessage;
import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.CategorieService;
import com.ipamc.election.services.PropositionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.services.VoteCategorieService;
import com.ipamc.election.services.VoteService;
import com.ipamc.election.views.components.CategoriesJury;
import com.ipamc.election.views.components.PropositionsJury;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;

@Route(value = "jury", layout = MainLayout.class)
@PageTitle("Salon de votes")

public class UserVotesView extends VerticalLayout implements BeforeEnterObserver  {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	private VoteService voteService;
	private Session session;
	private CategoriesJury cats;
	private PropositionsJury props;
	private TextField pickPseudo;
	private Question quest;
	private PropositionService propService;
	private CategorieService catService;
	private VoteCategorieService voteCatService;
	Registration broadcasterRegistration;
	
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register(newMessage -> {
        	if(newMessage.equals("ENABLE_VOTE")) {
        		ui.access(() -> cats.enableVotes());
        		ui.access(() -> sessionService.enableVoteQuestion());
        	}
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }
	
    public UserVotesView(UserService userService, SecurityUtils tools, SessionService sessionService, VoteService voteService, CategorieService catService, PropositionService propService, VoteCategorieService voteCatService) {
    	this.userService = userService;
    	this.propService = propService;
    	this.catService = catService;
    	this.voteCatService = voteCatService;
    	this.tools = tools;
    	this.sessionService = sessionService;
    	this.voteService = voteService;
    	H2 sessionName = new H2();
    	H4 info = new H4();
    	if(sessionService.checkSessionAccess(userService.getByUsername(tools.getAuthenticatedUser().getUsername()))) {
    		if(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).getHasJoinedSession()) {
    			
    			//code ici en attente d'une question...
    			
    			//CA CEST A METTRE DANS LE BROADCASTER;
    			/**
    			session = sessionService.getActiveSession();
    			quest = session.getActiveQuestion();
    			props = new PropositionsJury(quest);
    			cats = new CategoriesJury(quest, props, propService, userService, tools, catService, voteCatService);
    			add(cats, props);


    			Button leave = new Button("Quitter le salon");
    			leave.addClickListener(event -> {
    				userService.leavesSession(userService.getByUsername(tools.getAuthenticatedUser().getUsername()));
    				UI.getCurrent().getPage().reload();
    			});
    			Button submit = cats.getSubmit();
    			add(sessionName, info, submit, leave);*/
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

