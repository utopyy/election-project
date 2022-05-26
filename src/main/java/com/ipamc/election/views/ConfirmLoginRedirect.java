package com.ipamc.election.views;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.security.SecurityUtils;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;

@PageTitle("Login confirm")
@Route(value = "login_confirm")
@RouteAlias(value = "")

public class ConfirmLoginRedirect extends VerticalLayout implements BeforeEnterObserver {

	private SecurityUtils tools;
	
	public ConfirmLoginRedirect(SecurityUtils tools) {
		this.tools = tools;
		setSpacing(false);
        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);
        UserDetails uD = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        add(new H2("This place intentionally left empty, "+uD.getUsername()));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
	   	String role = "";	
	   	if(tools.getAuthenticatedUser()!=null) {	
	    	role = tools.getAuthenticatedUser().getAuthorities().iterator().next().getAuthority();
	    	if(!RouteConfiguration.forSessionScope().isRouteRegistered(UserVotesView.class) && !RouteConfiguration.forSessionScope().isRouteRegistered(AdminVotesView.class))
	    	tools.configRouter(role);	
	    	if(role.equals(EnumRole.ROLE_USER.toString())) {
		    	beforeEnterEvent.forwardTo(UserVotesView.class);
		    }else{
		    	beforeEnterEvent.forwardTo(AdminVotesView.class);
		    }
	   	}
	}
}
