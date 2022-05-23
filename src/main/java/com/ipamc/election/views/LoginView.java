package com.ipamc.election.views;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.security.SecurityUtils;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@PageTitle("Login")
@Route(value = "login", layout = MainLayout.class)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
	
    private LoginForm login = new LoginForm(); 
    private SecurityUtils tools;

    public LoginView(SecurityUtils tools){
    	this.tools = tools;
        login.setAction("login"); 
        getElement().appendChild(login.getElement());
    }
    
    @Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    	if(tools.getAuthenticatedUser()!=null) {
	    	String role = tools.getAuthenticatedUser().getAuthorities().iterator().next().getAuthority();
	    	if(role.equals(EnumRole.ROLE_USER.toString())) {
	    		beforeEnterEvent.forwardTo(VotesView.class);
	    	}else if(role.equals(EnumRole.ROLE_ADMIN.toString()) || role.equals(EnumRole.ROLE_SUPER_ADMIN.toString())) {
	    		beforeEnterEvent.forwardTo(SalonVotesView.class);
	    	}
		}
    }
}
