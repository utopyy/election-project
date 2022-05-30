package com.ipamc.election.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Role;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.security.services.UserDetailsImpl;
import com.ipamc.election.security.services.UserDetailsServiceImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
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
    	
    	LoginI18n log = LoginI18n.createDefault();
    	log.getForm().setUsername("Pseudo");
    	log.getForm().setForgotPassword("Mot de passe oublié?");
    	log.getForm().setPassword("Mot de passe");
    	log.getForm().setSubmit("Se connecter");
    	log.getForm().setTitle("Connexion");
        login.setAction("login"); 
        getElement().appendChild(login.getElement());
        login.addForgotPasswordListener(event ->{
        	UI.getCurrent().navigate(ForgetPasswordView.class);
        });
        
        login.setI18n(log);
       setHorizontalComponentAlignment(Alignment.CENTER, login);
    }
    
    @Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    	if(tools.getAuthenticatedUser()!=null) {	
		    	beforeEnterEvent.forwardTo(ConfirmLoginRedirect.class);
    	}
    }
}

		
	

