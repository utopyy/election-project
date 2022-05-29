package com.ipamc.election.views;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "jury", layout = MainLayout.class)
@PageTitle("Votes")


public class UserVotesView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	
    public UserVotesView(UserService userService, SecurityUtils tools) {
    	this.userService = userService;
    	this.tools = tools;
        setSpacing(false);
        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);
        //UserDetails uD = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        add(new H2("This place intentionally left empty, "/*+uD.getUsername()*/));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

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
