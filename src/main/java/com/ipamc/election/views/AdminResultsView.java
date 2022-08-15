package com.ipamc.election.views;

import com.ipamc.election.data.entity.ResultatsJury;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.ResultatsJuryService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "results", layout = MainLayout.class)
@PageTitle("Résultats")

public class AdminResultsView extends VerticalLayout implements BeforeEnterObserver {
	
	private UserService userService;
	private ResultatsJuryService resultsService;
	private SecurityUtils tools;

	private ResultatsJury results;
	
    public AdminResultsView(UserService userService, ResultatsJuryService resultsService, SecurityUtils tools) {
 
    	this.userService = userService;
    	this.resultsService = resultsService;
    	this.tools = tools;
    	results = resultsService.getLastResults();
    	initView();    
    }
    
    private void initView() {
    	if(results!=null) {
    		// show homescreen ui with "Show last votes" button
    	}else {
    		add(new Label("No votes"));
    		// show UI "No votes are available"
    	}
    }
    
    private VerticalLayout showLastVotes() {
    	// show all votes from results
    	// show clear all button
    	return null;
    }

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}
		if(tools.getAuthenticatedUser().getAuthorities().iterator().next().toString().equals("ROLE_USER")) {
			beforeEnterEvent.forwardTo("jury");
		}

	}

}


