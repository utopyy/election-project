package com.ipamc.election.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Role;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.security.services.UserDetailsImpl;
import com.ipamc.election.security.services.UserDetailsServiceImpl;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;



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
    	String role = "";
    	if(tools.getAuthenticatedUser()!=null) {	
	    	role = tools.getAuthenticatedUser().getAuthorities().iterator().next().getAuthority();
	    	if(role.equals(EnumRole.ROLE_USER.toString())) {	
		    	beforeEnterEvent.forwardTo(UserVotesView.class);
		    }else{
		    	beforeEnterEvent.forwardTo(AdminVotesView.class);
		    }
    	}
    }
}

		
	

